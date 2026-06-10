package com.hooman.einkaufszettel.feature.presentation.create_bill

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hooman.einkaufszettel.core.network.ConnectivityObserver
import com.hooman.einkaufszettel.core.presentation.UiText
import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.model.Bill
import com.hooman.einkaufszettel.domain.repository.AuthRepository
import com.hooman.einkaufszettel.domain.usecase.InsertBillToLocalUseCase
import com.hooman.einkaufszettel.domain.usecase.InsertBillToRemoteUseCase
import einkaufszettel.composeapp.generated.resources.Res
import einkaufszettel.composeapp.generated.resources.bill_added_to_remote_successfully
import einkaufszettel.composeapp.generated.resources.bill_is_null
import einkaufszettel.composeapp.generated.resources.data_save_just_in_local
import einkaufszettel.composeapp.generated.resources.user_is_not_logged_in
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class CreateBillViewModel(
    private val insertBillL: InsertBillToLocalUseCase,
    private val insertBillR: InsertBillToRemoteUseCase,
    private val authRepository: AuthRepository,
    private val observer: ConnectivityObserver
): ViewModel() {
    private val _createListState = MutableStateFlow(CreateBillState())
    val createListState = _createListState.asStateFlow()

    fun addBillIntoLocal(bill: Bill?){
        _createListState.value = _createListState.value.copy(
            isLoading = true
        )
        viewModelScope.launch {
            if(bill == null){
                _createListState.value = _createListState.value.copy(
                    isLoading = false,
                    error = UiText.StringResourceId(Res.string.bill_is_null)
                )
            }else{
                val userId = authRepository.getCurrentUserId()
                if(userId == null){
                    _createListState.value = _createListState.value.copy(
                        isLoading = false,
                        error = UiText.StringResourceId(Res.string.user_is_not_logged_in)
                    )
                    return@launch
                }
                val finalBill = bill.copy(userId = userId)
                val localResult = insertBillL.invoke(finalBill)

                if(localResult is Resource.Error){
                    _createListState.value = _createListState.value.copy(
                        isLoading = false,
                        error = UiText.DynamicString(localResult.message ?: "Unknown Error")
                    )
                    return@launch
                }

                val online = observer.isConnected.first()

                if(!online){
                    _createListState.value = _createListState.value.copy(
                        isLoading = false,
                        error = UiText.StringResourceId(Res.string.data_save_just_in_local)
                    )
                    return@launch
                }

                val remoteResult = insertBillR(finalBill)
                when(remoteResult){
                    is Resource.Success ->{
                        _createListState.value = _createListState.value.copy(
                            isLoading = false,
                            error = UiText.StringResourceId(Res.string.bill_added_to_remote_successfully)
                        )
                    }
                    is Resource.Error ->{
                        _createListState.value = _createListState.value.copy(
                            isLoading = false,
                            error = UiText.DynamicString(remoteResult.message ?: "Unknown Error")
                        )
                    }
                    is Resource.Loading ->{
                        _createListState.value = _createListState.value.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                }
            }
        }

    }
}