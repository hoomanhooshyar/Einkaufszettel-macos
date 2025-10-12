package com.hooman.einkaufszettel.core.presentation

import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import com.hooman.einkaufszettel.app.Routes

fun NavDestination.hasRoute(route: Routes): Boolean{
    return this.route == route::class.qualifiedName || this.hasRoute(route::class)
}

fun NavController.navigateSingleTopTo(route: Routes){
    this.navigate(route){
        popUpTo(Routes.MainGraph){saveState = true}
        launchSingleTop = true
        restoreState = true
    }
}