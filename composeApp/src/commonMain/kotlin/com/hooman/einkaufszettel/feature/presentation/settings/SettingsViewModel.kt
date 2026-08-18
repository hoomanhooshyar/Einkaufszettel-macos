package com.hooman.einkaufszettel.feature.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.core.util.changeLanguage
import com.hooman.einkaufszettel.domain.repository.AuthRepository
import com.hooman.einkaufszettel.domain.repository.SettingsPreferences
import com.hooman.einkaufszettel.feature.presentation.login.util.GoogleTokens
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsPreferences: SettingsPreferences,
    private val auth: AuthRepository
): ViewModel() {

    private val _settingState = MutableStateFlow(SettingsState())
    val settingState: StateFlow<SettingsState> = _settingState.asStateFlow()

    var getInfoJob: Job? = null

    init {
        setLanguage()
        getUserInfo()
    }

    private fun setLanguage(){
        viewModelScope.launch {
            settingsPreferences.languageFlow.collect { savedLanguage ->
                _settingState.update { currentState ->
                    currentState.copy(currentLanguage = savedLanguage)
                }
            }
        }
    }

    private fun getUserInfo(){
        getInfoJob?.cancel()
        getInfoJob = viewModelScope.launch {
            if(auth.getCurrentUser() != null){
                _settingState.value = _settingState.value.copy(
                    user = auth.getCurrentUser()
                )
            }
        }

    }


    fun onGoogleIdTokenReceived(googleToken: GoogleTokens) {
        viewModelScope.launch {
            _settingState.value = _settingState.value.copy(isLoading = true)

            val result =
                auth.signInWithGoogle(googleToken.idToken, googleToken.accessToken)

            when (result) {
                is Resource.Success -> {
                    _settingState.value = _settingState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                    )
                }

                is Resource.Error -> {
                    _settingState.value = _settingState.value.copy(
                        isLoading = false,
                        isLoggedIn = false,
                        error = result.message
                    )


                }

                is Resource.Loading -> {
                    _settingState.value = _settingState.value.copy(
                        isLoading = true,
                        error = null
                    )
                }
            }
        }
    }

    fun onLanguageSelected(languageCode: String){
        viewModelScope.launch {
            settingsPreferences.saveLanguage(languageCode)
            changeLanguage(languageCode)
        }
    }

    fun userLogout(){
        viewModelScope.launch {
            val result = auth.signOut()
            if(result is Resource.Success){
                _settingState.value = _settingState.value.copy(isLoggedIn = false)
            }
        }
    }
}