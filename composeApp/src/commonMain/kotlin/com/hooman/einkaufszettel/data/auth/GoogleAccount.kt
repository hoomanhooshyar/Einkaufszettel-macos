package com.hooman.einkaufszettel.data.auth

data class GoogleAccount(
    val token: String,
    val displayName: String? = null,
    val profileImageUrl: String? = null
)
