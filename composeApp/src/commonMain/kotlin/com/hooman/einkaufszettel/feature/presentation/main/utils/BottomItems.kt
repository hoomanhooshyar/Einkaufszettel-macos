package com.hooman.einkaufszettel.feature.presentation.main.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import com.hooman.einkaufszettel.app.Routes
import com.hooman.einkaufszettel.core.presentation.UiText
import einkaufszettel.composeapp.generated.resources.Res
import einkaufszettel.composeapp.generated.resources.home
import einkaufszettel.composeapp.generated.resources.house
import einkaufszettel.composeapp.generated.resources.product
import einkaufszettel.composeapp.generated.resources.reports
import einkaufszettel.composeapp.generated.resources.settings

object BottomItems {
    fun default() = listOf(
        BottomItem(Routes.Home, Res.string.home, Icons.Default.Home),
        BottomItem(Routes.Products, Res.string.product, Icons.Default.ShoppingCart),
        BottomItem(Routes.Reports, Res.string.reports, Icons.Default.Home),
        BottomItem(Routes.Settings, Res.string.settings, Icons.Default.Settings)
    )
}