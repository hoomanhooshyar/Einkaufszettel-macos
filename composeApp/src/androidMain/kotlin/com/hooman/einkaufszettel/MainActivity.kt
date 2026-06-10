package com.hooman.einkaufszettel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import com.hooman.einkaufszettel.app.App
import com.google.firebase.FirebaseApp
import com.hooman.einkaufszettel.feature.utils.LocalPlatformContext
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        setContent {
            CompositionLocalProvider(LocalPlatformContext provides LocalContext.current) {
                App()
            }

        }
    }
}