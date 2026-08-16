package com.hooman.einkaufszettel.feature.presentation.main.utils

import androidx.compose.ui.graphics.vector.ImageVector
import com.hooman.einkaufszettel.app.Routes
import org.jetbrains.compose.resources.StringResource

data class BottomItem(
    val route: Routes,
    val label: StringResource,
    val icon: ImageVector
)
