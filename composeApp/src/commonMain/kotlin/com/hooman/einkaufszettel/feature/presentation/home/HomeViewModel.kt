package com.hooman.einkaufszettel.feature.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hooman.einkaufszettel.core.network.ConnectivityObserver
import com.hooman.einkaufszettel.core.presentation.UiText
import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.model.Bill
import com.hooman.einkaufszettel.domain.repository.AuthRepository
import com.hooman.einkaufszettel.domain.source.FirebaseService
import com.hooman.einkaufszettel.domain.usecase.GetAllBillsByUserIdFromRemoteUseCase
import com.hooman.einkaufszettel.feature.presentation.home.addBill

import com.hooman.einkaufszettel.domain.usecase.GetAllBillsFromLocalUseCase
import com.hooman.einkaufszettel.domain.usecase.GetBillByIdFromLocalUseCase
import com.hooman.einkaufszettel.domain.usecase.GetBillByIdFromRemoteUseCase
import com.hooman.einkaufszettel.domain.usecase.InsertBillToLocalUseCase
import dev.gitlive.firebase.firestore.FirebaseFirestore
import einkaufszettel.composeapp.generated.resources.Res
import einkaufszettel.composeapp.generated.resources.not_logged_in
import einkaufszettel.composeapp.generated.resources.unknown_error
import kotlinx.coroutines.ExperimentalCoroutinesApi

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource


@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val getBillL: GetAllBillsFromLocalUseCase,
    private val getBillR: GetAllBillsByUserIdFromRemoteUseCase,
    private val insertBillL: InsertBillToLocalUseCase,
    private val getBillByIdL: GetBillByIdFromLocalUseCase,
    private val auth: AuthRepository,
    private val observer: ConnectivityObserver,
    private val svc: FirebaseService
): ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state

    init {
        viewModelScope.launch {
            runCatching { svc.requiredUserId() }
            addBill(svc)
            addProduct(svc)
        }
    }


    fun observeBills(){
        //Get Data from Local
        viewModelScope.launch {
            getBillL().collect { res ->
                when(res){
                    is Resource.Success -> {
                        _state.value = _state.value.copy(
                            bills = res.data!!,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(
                            bills = emptyList(),
                            isLoading = false,
                            errorMessage = if(res.message == null)
                                UiText.StringResourceId(Res.string.unknown_error)
                            else
                                UiText.DynamicString(res.message)
                        )
                    }
                    is Resource.Loading<*> -> {
                        _state.value = _state.value.copy(
                            bills = emptyList(),
                            errorMessage = null,
                            isLoading = true
                        )
                    }
                }
            }
        }

        //Get Data from Remote
        viewModelScope.launch {
            observer.isConnected
                .onStart { emit(false)}
                .distinctUntilChanged()
                .collect { online ->
                    if(!online) return@collect
                    val uid = auth.getCurrentUserId()
                    if(uid == null){
                        _state.value = _state.value.copy(
                            isLoading = false,
                            errorMessage = UiText.StringResourceId(Res.string.not_logged_in)
                        )
                        return@collect
                    }
                    getBillR(uid).collect { res ->
                        when(res){
                            is Resource.Success -> {
                                res.data?.forEach { bill ->
                                    insertBillL(bill).collect {  }
                                }
                            }
                            is Resource.Error -> {
                                _state.value = _state.value.copy(
                                    errorMessage = if(res.message == null)
                                        UiText.StringResourceId(Res.string.unknown_error)
                                    else UiText.DynamicString(res.message)
                                )
                            }
                            is Resource.Loading -> Unit
                        }
                    }

                }
        }
    }
}