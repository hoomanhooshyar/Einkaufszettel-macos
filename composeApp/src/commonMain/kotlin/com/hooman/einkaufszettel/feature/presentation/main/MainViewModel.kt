package com.hooman.einkaufszettel.feature.presentation.main

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hooman.einkaufszettel.core.network.ConnectivityObserver
import com.hooman.einkaufszettel.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val observer: ConnectivityObserver,
    private val auth: AuthRepository
): ViewModel() {

    private val _loginState = MutableStateFlow<Boolean>(false)

    val loginState: StateFlow<Boolean> = _loginState

    val isConnected = observer
        .isConnected
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            false
        )

    fun checkLogin(){
        viewModelScope.launch {
            val test = auth.getCurrentUserId()
            _loginState.value = auth.getCurrentUserId() != null

        }
    }



}