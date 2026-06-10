package com.hooman.einkaufszettel.feature.presentation.login.util

interface GoogleAuthManager {
    suspend fun signIn(environment: Any?): GoogleTokens?
}