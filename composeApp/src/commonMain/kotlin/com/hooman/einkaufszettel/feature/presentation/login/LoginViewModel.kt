package com.hooman.einkaufszettel.feature.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.repository.AuthRepository
import com.hooman.einkaufszettel.feature.presentation.login.util.GoogleAuthManager
import com.hooman.einkaufszettel.feature.presentation.login.util.GoogleTokens
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val googleAuthManager: GoogleAuthManager
) : ViewModel() {
    private val _loginState = MutableStateFlow(LoginState())
    val loginState = _loginState.asStateFlow()




    fun onGoogleIdTokenReceived(googleToken: GoogleTokens) {
        viewModelScope.launch {
            _loginState.update { it.copy(isLoading = true) }

            val result =
                authRepository.signInWithGoogle(googleToken.idToken, googleToken.accessToken)

            when (result) {
                is Resource.Success -> {
                    _loginState.value = _loginState.value.copy(
                            isLoading = false,
                            isLoggedIn = true,
                            )
                }

                is Resource.Error -> {
                    _loginState.value = _loginState.value.copy(
                        isLoading = false,
                        isLoggedIn = false,
                        error = result.message
                    )


                }

                is Resource.Loading -> {
                    _loginState.value = _loginState.value.copy(
                        isLoading = true,
                        error = null
                    )
                }
            }
        }
    }

    fun clearMessage(){
        _loginState.value = _loginState.value.copy(
            error = null
        )
    }

    fun resetLoginStatus(){
        _loginState.value = _loginState.value.copy(
            isLoading = false
        )
    }
}