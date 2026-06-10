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
): ViewModel() {
    private val _loginState = MutableStateFlow(LoginState())
    val loginState = _loginState.asStateFlow()

    private val _events = Channel<LoginEvent>()
    val events = _events.receiveAsFlow()


    fun onGoogleIdTokenReceived(googleToken: GoogleTokens){
        viewModelScope.launch {
            _loginState.update { it.copy(isLoading = true) }

            authRepository.signInWithGoogle(googleToken.idToken, googleToken.accessToken)
                .collect { res ->
                    when(res){
                        is Resource.Success -> {
                            _loginState.update {
                                it.copy(
                                isLoading = false,
                                isLoggedIn = true,

                                )
                            }
                            _events.send(LoginEvent.NavigateToHome)
                        }
                        is Resource.Error -> {
                            _loginState.update {
                                it.copy(
                                    isLoading = false
                                )
                            }
                            _events.send(LoginEvent.ShowSnackBar(res.message!!))
                        }
                        is Resource.Loading -> {
                            _loginState.update { it.copy(isLoading = true) }
                        }
                    }
                }
        }
    }
}