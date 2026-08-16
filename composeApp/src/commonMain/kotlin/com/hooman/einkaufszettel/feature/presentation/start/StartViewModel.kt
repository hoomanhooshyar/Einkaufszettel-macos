package com.hooman.einkaufszettel.feature.presentation.start

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hooman.einkaufszettel.app.Routes
import com.hooman.einkaufszettel.core.network.ConnectivityObserver
import com.hooman.einkaufszettel.domain.repository.AuthRepository
import com.hooman.einkaufszettel.domain.usecase.SyncDatabaseUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class StartViewModel(
    private val authRepository: AuthRepository,
    private val observer: ConnectivityObserver,
    private val syncUseCase: SyncDatabaseUseCase
): ViewModel() {
    private val _startState = MutableStateFlow(StartState())
    val startState: StateFlow<StartState> = _startState.asStateFlow()

    init {
        checkStartupLogic()
    }

    private fun checkStartupLogic(){
        viewModelScope.launch {

            val login = authRepository.getCurrentUserId()

            val online = observer.isConnected.first()

            if(login.isNullOrEmpty()){
                _startState.value = _startState.value.copy(
                    nexDestination = Routes.Login
                )
                return@launch
            }

            if(online){
                try {
                    syncUseCase()
                }catch (e: Exception){
                    e.printStackTrace()
                }

            }else{
                _startState.value = _startState.value.copy(
                    nexDestination = Routes.Home
                )
            }

            _startState.value = _startState.value.copy(nexDestination = Routes.Home)
        }
    }
}