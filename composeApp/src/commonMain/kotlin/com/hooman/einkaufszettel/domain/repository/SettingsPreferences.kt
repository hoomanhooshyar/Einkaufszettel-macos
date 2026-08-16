package com.hooman.einkaufszettel.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsPreferences {
    val languageFlow: Flow<String>
    suspend fun saveLanguage(languageCode: String)
}