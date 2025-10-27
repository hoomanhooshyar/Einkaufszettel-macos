package com.hooman.einkaufszettel.feature.presentation.login.util




import android.content.Context

import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest

import com.google.android.libraries.identity.googleid.GetGoogleIdOption

import com.hooman.einkaufszettel.data.auth.GoogleAccount

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential



@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class GoogleSignInProviderImpl(
    private val context: Context,
    private val webClient: String
) : GoogleSignInProvider {

    actual override suspend fun signIn(): GoogleAccount = withContext(Dispatchers.Main){
        val credentialManager = CredentialManager.create(context)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(webClient)
            .setFilterByAuthorizedAccounts(false)
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val result = credentialManager.getCredential(context,request)
        val credential = result.credential

        if(credential is GoogleIdTokenCredential){
            val idToken = credential.idToken

            GoogleAccount(
                token = idToken,
                displayName = credential.displayName.orEmpty(),
                profileImageUrl = credential.profilePictureUri?.toString()

            )
        }else{
            error("Unknown credential type")
        }


    }
}