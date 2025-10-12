package com.hooman.einkaufszettel.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

enum class PurchaseType(val icon: ImageVector) {
    SUPERMARKET(Icons.Filled.ShoppingCart),
    PARTY(Icons.Filled.Celebration),
    HOUSE(Icons.Filled.Home),
    FRIENDS(Icons.Filled.People),
    CLOTH(Icons.Filled.Checkroom),
    OTHER(Icons.Filled.Category)
}