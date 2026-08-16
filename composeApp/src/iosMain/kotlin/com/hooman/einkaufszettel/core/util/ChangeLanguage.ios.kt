package com.hooman.einkaufszettel.core.util

import platform.Foundation.NSUserDefaults

actual fun changeLanguage(languageCode: String) {
    NSUserDefaults.standardUserDefaults.setObject(listOf(languageCode), "AppleLanguages")
    NSUserDefaults.standardUserDefaults.synchronize()
}