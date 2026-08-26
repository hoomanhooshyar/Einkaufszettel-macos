package com.hooman.einkaufszettel.domain.model

import com.hooman.einkaufszettel.core.util.KeepForFirebase

@KeepForFirebase
data class User(
    val id: String,
    val name: String? = null,
    val imageUrl: String? = null
)
