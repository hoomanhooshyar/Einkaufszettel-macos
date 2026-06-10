package com.hooman.einkaufszettel.feature.presentation.login.util


import android.content.Context

import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest

import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.hooman.einkaufszettel.R


class GoogleAuthManagerAndroidImpl: GoogleAuthManager {
    override suspend fun signIn(environment: Any?): GoogleTokens? {
        val context = environment as? Context ?: return null

        val credentialManager = CredentialManager.create(context)
        val webClient = context.getString(R.string.default_web_client_id)

        val googleOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(webClient)
            .setAutoSelectEnabled(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleOption)
            .build()

        return try {
            val result = credentialManager.getCredential(context, request)
            val credential = result.credential

            if(credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL){
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                googleIdTokenCredential.idToken
                GoogleTokens(
                    googleIdTokenCredential.idToken,
                    null
                )
            }else{
                null
            }
        }catch (e: Exception){
            e.printStackTrace()
            null
        }
    }

}