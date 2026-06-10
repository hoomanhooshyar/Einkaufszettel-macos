package com.hooman.einkaufszettel.feature.presentation.login.util

import cocoapods.GoogleSignIn.GIDSignIn
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import platform.posix.err
import kotlin.coroutines.resume
@OptIn(ExperimentalForeignApi::class)
class GoogleAuthManagerIOSImpl: GoogleAuthManager {

    override suspend fun signIn(environment: Any?): GoogleTokens? {
        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
            ?: throw IllegalArgumentException("Environment must be UIViewController in iOS")
        return suspendCancellableCoroutine{ continuation ->
            GIDSignIn.sharedInstance.signInWithPresentingViewController(
                presentingViewController = rootViewController
            ){ signInResult, error ->
                if(error != null){
                    continuation.resume(null)
                    return@signInWithPresentingViewController
                }

                val idToken = signInResult?.user?.idToken?.tokenString
                val accessToken = signInResult?.user?.accessToken?.tokenString

                if(idToken != null && accessToken != null){
                    continuation.resume(GoogleTokens(idToken, accessToken))
                }else{
                    continuation.resume(null)
                }
            }
        }
    }

}