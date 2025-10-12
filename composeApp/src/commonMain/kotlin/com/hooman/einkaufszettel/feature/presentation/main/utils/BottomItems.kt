package com.hooman.einkaufszettel.feature.presentation.main.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import com.hooman.einkaufszettel.app.Routes

object BottomItems {
    fun default() = listOf(
        BottomItem(Routes.Home,"Home", Icons.Default.Home),
        BottomItem(Routes.Products,"Product", Icons.Default.ShoppingCart),
        BottomItem(Routes.Reports,"Reports", Icons.Default.Home),
        BottomItem(Routes.Settings,"Settings", Icons.Default.Settings)
    )
}