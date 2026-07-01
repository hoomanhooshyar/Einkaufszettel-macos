package com.hooman.einkaufszettel.core.util

import kotlin.math.roundToInt

fun Double.toTwoDecimals(): String{
    val rounded = (this * 100).roundToInt() / 100.0

    val parts = rounded.toString().split(".")
    val integerPart = parts[0]
    val fractionPart = if(parts.size > 1) parts[1] else ""

    return "$integerPart.${fractionPart.padEnd(2,'0')}"
}