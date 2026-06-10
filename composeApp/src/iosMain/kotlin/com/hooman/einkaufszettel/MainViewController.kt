package com.hooman.einkaufszettel

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.interop.LocalUIViewController
import androidx.compose.ui.window.ComposeUIViewController
import com.hooman.einkaufszettel.app.App
import com.hooman.einkaufszettel.core.di.initKoin
import com.hooman.einkaufszettel.feature.utils.LocalPlatformContext
import dev.gitlive.firebase.FirebaseApp

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) {
    val viewController = LocalUIViewController.current
    CompositionLocalProvider(LocalPlatformContext provides viewController){
        App()
    }
}