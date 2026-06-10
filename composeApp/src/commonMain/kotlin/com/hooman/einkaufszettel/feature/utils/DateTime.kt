package com.hooman.einkaufszettel.feature.utils

expect object DateTime{
    fun getFormattedDate(
        timestamp: String
    ): String
}