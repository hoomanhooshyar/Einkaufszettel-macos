package com.hooman.einkaufszettel.feature.presentation.login

sealed interface LoginEvent {
    data object NavigateToHome: LoginEvent
    data class ShowSnackBar(val message: String): LoginEvent
}