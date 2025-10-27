package com.hooman.einkaufszettel.feature.presentation.login.util

import com.hooman.einkaufszettel.data.auth.GoogleAccount

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class GoogleSignInProviderImpl: GoogleSignInProvider {
    override suspend fun signIn(): GoogleAccount
}