package com.hooman.einkaufszettel.feature.presentation.login.util

interface GoogleAuthCallback {
    fun onLoginSuccess(idToken: String)
    fun onLoginFailed(errorMessage: String)
}