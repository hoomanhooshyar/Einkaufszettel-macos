package com.hooman.einkaufszettel.feature.presentation.main.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.hooman.einkaufszettel.core.presentation.darkOrangeColor
import com.hooman.einkaufszettel.core.presentation.navigateSingleTopTo
import com.hooman.einkaufszettel.feature.presentation.main.utils.BottomItem
import com.hooman.einkaufszettel.feature.presentation.main.utils.selectedTabQualifiedName
import einkaufszettel.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.stringResource

@Composable
fun BottomBar(
    navController: NavController,
    items: List<BottomItem>
) {
    val entry by navController.currentBackStackEntryAsState()
    val currentDestination = entry?.destination
    NavigationBar {
        items.forEach { item ->
            val selected = currentDestination?.route?.contains(item.route::class.simpleName ?: "") == true
            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigateSingleTopTo(item.route)
                },
                icon = {Icon(imageVector = item.icon, contentDescription = stringResource(item.label))},
                label = { Text(
                    text = stringResource(item.label),
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = darkOrangeColor,
                    selectedTextColor = darkOrangeColor,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}