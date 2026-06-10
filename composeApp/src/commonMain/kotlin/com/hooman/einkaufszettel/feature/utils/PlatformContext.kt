package com.hooman.einkaufszettel.feature.utils

import androidx.compose.runtime.staticCompositionLocalOf

val LocalPlatformContext = staticCompositionLocalOf<Any>{
    error("Platform context not provided")
}