package com.hooman.einkaufszettel.core.presentation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hooman.einkaufszettel.app.Routes
import com.hooman.einkaufszettel.feature.presentation.main.MainScreen
import com.hooman.einkaufszettel.feature.presentation.start.StartScreenRoot

@Composable
fun RootNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.Start,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        composable<Routes.Start> {
            val snackBarHostState = remember { SnackbarHostState() }
            Box(modifier = Modifier.fillMaxSize()) {
                StartScreenRoot(
                    snackBarHostState = snackBarHostState,
                    navController = navController
                )
                SnackbarHost(
                    hostState = snackBarHostState,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
        composable<Routes.MainContainer> {
            MainScreen()
        }
    }
}
