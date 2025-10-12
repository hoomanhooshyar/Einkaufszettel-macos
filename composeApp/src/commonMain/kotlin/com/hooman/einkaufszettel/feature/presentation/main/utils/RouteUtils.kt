package com.hooman.einkaufszettel.feature.presentation.main.utils

import com.hooman.einkaufszettel.app.Routes

private val HOME_ON = Routes.Home::class.qualifiedName
private val PRODUCT_ON = Routes.Products::class.qualifiedName
private val CREATE_ON = Routes.CreateList::class.qualifiedName
private val DETAILS_ON = Routes.ListDetails::class.qualifiedName
private val REPORTS_ON = Routes.Reports::class.qualifiedName
private val SETTINGS_ON = Routes.Settings::class.qualifiedName

fun selectedTabQualifiedName(currentRoute: String?): String?{
    return when(currentRoute){
        HOME_ON,CREATE_ON,DETAILS_ON -> HOME_ON
        PRODUCT_ON -> PRODUCT_ON
        REPORTS_ON -> REPORTS_ON
        SETTINGS_ON -> SETTINGS_ON
        else -> null
    }
}