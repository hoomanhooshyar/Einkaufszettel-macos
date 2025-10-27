package com.hooman.einkaufszettel.domain.model

data class User(
    val id: String,
    val name: String? = null,
    val imageUrl: String? = null
)
