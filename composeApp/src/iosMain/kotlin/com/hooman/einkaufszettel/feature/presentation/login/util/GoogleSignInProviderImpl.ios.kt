package com.hooman.einkaufszettel.feature.presentation.login.util

import cocoapods.FirebaseCore.FIRApp
import cocoapods.GoogleSignIn.GIDConfiguration
import com.hooman.einkaufszettel.data.auth.GoogleAccount
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSError
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import cocoapods.GoogleSignIn.GIDSignIn
import cocoapods.GoogleSignIn.GIDSignInResult
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIWindow
import platform.UIKit.UIWindowScene
import cocoapods.GoogleSignIn.GIDGoogleUser
import cocoapods.GoogleSignIn.GIDProfileData
import platform.Foundation.objectEnumerator
import platform.Foundation.NSSet
import platform.Foundation.allObjects
import platform.posix.err
import threadUtils.callstack


@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class GoogleSignInProviderImpl :
    GoogleSignInProvider {
    @OptIn(ExperimentalForeignApi::class)
    actual override suspend fun signIn(): GoogleAccount =
        suspendCancellableCoroutine { cont ->
            val root = findTopViewController()
            if (root == null) {
                cont.resumeWithException(Exception("No view controller found"))
                return@suspendCancellableCoroutine
            }

            val signIn = GIDSignIn.sharedInstance()
            signIn.signInWithPresentingViewController(
                presentingViewController = root,
                completion = { result, error ->
                    when{
                        error != null -> cont.resumeWithException(Exception(error.localizedDescription))
                        result != null -> {
                            val user = result.user
                            val idToken = user.idToken?.tokenString
                            if(idToken == null){
                                cont.resumeWithException(IllegalStateException("No ID Token"))
                                return@signInWithPresentingViewController
                            }
                            val profile = user.profile
                            val displayName = profile?.name
                            val photoUrl = profile?.imageURLWithDimension(200uL)?.absoluteString.toString()

                            cont.resume(
                                GoogleAccount(
                                    token = idToken,
                                    displayName = displayName,
                                    profileImageUrl = photoUrl
                                )
                            )
                        }
                        else -> cont.resumeWithException(IllegalStateException("Unknown Google Sign In"))
                    }
                }
            )

        }
    private fun findTopViewController(): UIViewController? {
        // تلاش برای پیدا کردن روت فعلی برای پرزنت UI لاگین
        val scenes = UIApplication.sharedApplication.connectedScenes
        val all = (scenes as NSSet).allObjects
        for (scene in all) {
            val windowScene = scene as? UIWindowScene ?: continue
            val windows = windowScene.windows
            val keyWindow = windows.firstOrNull {(it as UIWindow).isKeyWindow()} as? UIWindow
            val root = keyWindow?.rootViewController
            if(root != null) return topMost(from = root)
        }
        val legacyRoot = UIApplication.sharedApplication.keyWindow?.rootViewController
        return legacyRoot?.let { topMost(from = it) }
    }

    private fun topMost(from: UIViewController): UIViewController {
        var current = from
        while (true) {
            val presented = current.presentedViewController ?: break
            current = presented
        }
        return current
    }
}