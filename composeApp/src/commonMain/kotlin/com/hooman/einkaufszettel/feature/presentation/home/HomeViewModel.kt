package com.hooman.einkaufszettel.feature.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hooman.einkaufszettel.core.network.ConnectivityObserver
import com.hooman.einkaufszettel.core.presentation.UiText
import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.repository.AuthRepository
import com.hooman.einkaufszettel.domain.usecase.GetAllBillsByUserIdFromRemoteUseCase

import com.hooman.einkaufszettel.domain.usecase.GetAllBillsFromLocalUseCase
import com.hooman.einkaufszettel.domain.usecase.GetBillByIdFromRemoteUseCase
import dev.gitlive.firebase.firestore.FirebaseFirestore
import einkaufszettel.composeapp.generated.resources.Res
import einkaufszettel.composeapp.generated.resources.unknown_error
import kotlinx.coroutines.ExperimentalCoroutinesApi

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource


@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val getBillL: GetAllBillsFromLocalUseCase,
    private val getBillR: GetAllBillsByUserIdFromRemoteUseCase,
    private val auth: AuthRepository,
    private val observer: ConnectivityObserver

): ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state

    fun observeBills(){
        viewModelScope.launch {
            observer.isConnected
                .distinctUntilChanged()
                .flatMapLatest { online ->
                    if(online){
                        val uid = auth.getCurrentUserId()
                        if(uid == null){
                            flowOf(Resource.Error(
                                message = UiText.StringResourceId(Res.string.unknown_error).toString(),
                                data = null
                                )
                            )
                        }else{
                            getBillR(uid)
                        }
                    }else{
                        getBillL()
                    }
                }

                .collect { res ->
                    when(res){
                        is Resource.Success -> {
                            _state.value = _state.value.copy(
                                bills = res.data ?: emptyList(),
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
                        is Resource.Loading -> {
                            _state.value = _state.value.copy(
                                bills = emptyList(),
                                errorMessage = null,
                                isLoading = true
                            )
                        }
                    }
                }
        }
    }
}