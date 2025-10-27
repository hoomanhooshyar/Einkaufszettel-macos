package com.hooman.einkaufszettel.feature.presentation.login.util

import com.hooman.einkaufszettel.data.auth.GoogleAccount

interface GoogleSignInProvider {
    suspend fun signIn(): GoogleAccount
}