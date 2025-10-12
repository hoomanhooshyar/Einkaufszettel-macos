package com.hooman.einkaufszettel.feature.presentation.main.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.hooman.einkaufszettel.app.Routes
import com.hooman.einkaufszettel.core.presentation.hasRoute

@Composable
fun shouldShowBottomBar(
    navController: NavController,
): Boolean {
    val entry by navController.currentBackStackEntryAsState()
    val dest = entry?.destination ?: return true

    return dest.hasRoute(Routes.Home)
            || dest.hasRoute(Routes.Products)
            || dest.hasRoute(Routes.Reports)
            || dest.hasRoute(Routes.Settings)
}