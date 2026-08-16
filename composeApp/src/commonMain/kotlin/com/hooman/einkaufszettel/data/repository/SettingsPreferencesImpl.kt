package com.hooman.einkaufszettel.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.hooman.einkaufszettel.domain.repository.SettingsPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsPreferencesImpl(
    private val dataStore: DataStore<Preferences>
) : SettingsPreferences {

    private val languageKey = stringPreferencesKey("app_language")

    override val languageFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[languageKey] ?: "en"
    }
    override suspend fun saveLanguage(languageCode: String) {
        dataStore.edit { preferences ->
            preferences[languageKey] = languageCode
        }
    }
}