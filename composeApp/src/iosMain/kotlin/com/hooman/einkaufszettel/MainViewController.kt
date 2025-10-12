package com.hooman.einkaufszettel

import androidx.compose.ui.window.ComposeUIViewController
import com.hooman.einkaufszettel.app.App
import com.hooman.einkaufszettel.core.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) {
    App()
}