package com.hooman.einkaufszettel.feature.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hooman.einkaufszettel.core.network.ConnectivityObserver
import com.hooman.einkaufszettel.core.presentation.UiText
import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.model.Bill
import com.hooman.einkaufszettel.domain.repository.AuthRepository
import com.hooman.einkaufszettel.domain.usecase.DeleteBillFromLocalUseCase
import com.hooman.einkaufszettel.domain.usecase.DeleteBillFromRemoteUseCase
import com.hooman.einkaufszettel.domain.usecase.GetAllBillsByUserIdFromRemoteUseCase
import com.hooman.einkaufszettel.domain.usecase.GetAllBillsFromLocalUseCase
import com.hooman.einkaufszettel.domain.usecase.InsertBillToLocalUseCase
import einkaufszettel.composeapp.generated.resources.Res
import einkaufszettel.composeapp.generated.resources.bill_is_null
import einkaufszettel.composeapp.generated.resources.bill_remove_local_fail
import einkaufszettel.composeapp.generated.resources.bill_remove_remote_fail
import einkaufszettel.composeapp.generated.resources.bill_remove_remote_success
import einkaufszettel.composeapp.generated.resources.no_bills
import einkaufszettel.composeapp.generated.resources.no_internet_error
import einkaufszettel.composeapp.generated.resources.not_logged_in
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val getBillL: GetAllBillsFromLocalUseCase,
    private val getBillR: GetAllBillsByUserIdFromRemoteUseCase,
    private val insertBillL: InsertBillToLocalUseCase,
    private val deleteBillL: DeleteBillFromLocalUseCase,
    private val deleteBillR: DeleteBillFromRemoteUseCase,
    private val auth: AuthRepository,
    private val observer: ConnectivityObserver,
): ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private var isInitialLoad = true

    private val _userId = MutableStateFlow(auth.getCurrentUserId())
    val userId = _userId.asStateFlow()

    init {
        observeBills()
    }

    private fun observeBills(){
        viewModelScope.launch {
            getBillL().collect { res ->
                when(res){
                    is Resource.Success ->{
                        val bills = res.data ?: emptyList()
                        if(bills.isEmpty() && isInitialLoad){
                            isInitialLoad = false
                            _state.value = _state.value.copy(isLoading = true)
                            val currentUser = _userId.value
                            if(!currentUser.isNullOrEmpty()){
                                getBillFromRemote()
                            }
                        }else{
                            isInitialLoad = false
                            val newTotalAmount = calculateTotalAmount(bills)
                            _state.value = _state.value.copy(
                                isLoading = false,
                                error = null,
                                bills = bills,
                                totalAmount = newTotalAmount
                            )
                        }
                    }
                    is Resource.Loading ->{
                        _state.value = _state.value.copy(
                            error = null,
                            isLoading = true
                        )
                    }
                    is Resource.Error ->{
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = UiText.DynamicString(res.message!!)
                        )
                    }
                }
            }
        }
    }

    private fun getBillFromRemote() {
        viewModelScope.launch {
            val online = observer.isConnected.first()
            if(!online){
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = UiText.StringResourceId(Res.string.no_internet_error)
                )
                return@launch
            }
            val userId = auth.getCurrentUserId()
            if (userId.isNullOrEmpty()) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = UiText.StringResourceId(Res.string.not_logged_in)
                )
                return@launch
            }
            getBillR(userId).collect { res ->
                when (res) {
                    is Resource.Success -> {
                        insertBillIntoLocal(res.data)
                    }

                    is Resource.Loading -> {}

                    is Resource.Error -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = UiText.DynamicString(res.message!!)
                        )
                    }
                }
            }
        }
    }

    private fun insertBillIntoLocal(bills: List<Bill>?){
        viewModelScope.launch {
            if(bills.isNullOrEmpty()){
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = UiText.StringResourceId(Res.string.no_bills),
                    bills = emptyList()
                )
                return@launch
            }
            bills.forEach { bill ->
                val result = insertBillL(bill)
                if(result is Resource.Error){
                    print("Error in inserting bill into local- ${result.message}")
                }
            }
        }
    }

    fun deleteBill(bill: Bill){
        _state.value = _state.value.copy(
            isLoading = true
        )

        viewModelScope.launch {
            val localResult = deleteBillL(bill)

            if(localResult is Resource.Error){
                _state.value = _state.value.copy(
                    error = UiText.StringResourceId(Res.string.bill_remove_local_fail)
                )
            }

            val online = observer.isConnected.first()

            if(!online){
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = UiText.StringResourceId(Res.string.not_logged_in)
                )
                return@launch
            }
            val remoteResult = deleteBillR(bill.id)

            if(remoteResult is Resource.Error){
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = UiText.StringResourceId(Res.string.bill_remove_remote_fail)
                )
            }

            _state.value = _state.value.copy(
                isLoading = false,
                error = UiText.StringResourceId(Res.string.bill_remove_remote_success)
            )
        }
    }

    private fun calculateTotalAmount(bills: List<Bill>): Double{
        return bills.sumOf { bill ->
            bill.items.sumOf { item ->
                item.itemCount * item.productPrice
            }
        }
    }
}