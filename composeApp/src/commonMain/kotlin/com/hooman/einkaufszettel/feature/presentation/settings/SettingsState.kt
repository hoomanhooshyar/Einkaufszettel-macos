package com.hooman.einkaufszettel.feature.presentation.settings

import com.hooman.einkaufszettel.domain.model.User

data class SettingsState(
    val user: User? = null,
    val email: String = "",
    val currentLanguage: String = "en"
)
