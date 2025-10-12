package com.hooman.einkaufszettel.feature.presentation.main.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.hooman.einkaufszettel.core.presentation.navigateSingleTopTo
import com.hooman.einkaufszettel.feature.presentation.main.utils.BottomItem
import com.hooman.einkaufszettel.feature.presentation.main.utils.selectedTabQualifiedName

@Composable
fun BottomBar(
    navController: NavController,
    items: List<BottomItem>
) {
    val entry by navController.currentBackStackEntryAsState()
    val currentRoute = entry?.destination?.route
    val selectedTabQN = selectedTabQualifiedName(currentRoute)
    NavigationBar {
        items.forEach { item ->
            val itemQN = item::class.qualifiedName
            val selected = selectedTabQN == itemQN

            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigateSingleTopTo(item.route)
                },
                icon = {Icon(imageVector = item.icon, contentDescription = item.label)},
                label = { Text(item.label) }
            )
        }
    }
}