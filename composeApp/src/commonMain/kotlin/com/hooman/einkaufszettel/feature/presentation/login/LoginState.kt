package com.hooman.einkaufszettel.feature.presentation.login

import com.hooman.einkaufszettel.core.presentation.UiText
import com.hooman.einkaufszettel.domain.model.User

data class LoginState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: String? = null
)
