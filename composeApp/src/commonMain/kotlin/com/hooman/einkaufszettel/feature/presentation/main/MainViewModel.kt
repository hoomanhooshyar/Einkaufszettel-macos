package com.hooman.einkaufszettel.feature.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hooman.einkaufszettel.core.network.ConnectivityObserver
import com.hooman.einkaufszettel.domain.repository.AuthRepository
import com.hooman.einkaufszettel.domain.repository.SettingsPreferences
import com.hooman.einkaufszettel.domain.usecase.SyncDatabaseUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val observer: ConnectivityObserver,
    private val auth: AuthRepository,
    private val settingsPreferences: SettingsPreferences,
    private val syncUseCase: SyncDatabaseUseCase
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

     val currentLanguage = settingsPreferences.languageFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "en"
        )

    init {
        syncOnStartup()

    }

    private fun syncOnStartup(){
        viewModelScope.launch {
            val online = observer.isConnected.first()
            if(online){
                try {
                    syncUseCase()
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }
    }

    fun checkLogin(){
        viewModelScope.launch {
            val test = auth.getCurrentUserId()
            _loginState.value = auth.getCurrentUserId() != null

        }
    }
}