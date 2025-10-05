package com.hooman.einkaufszettel.domain.model

import kotlinx.datetime.Instant

data class Bill(
    val id: String,
    val billDate: Instant,
    val items: List<ShoppingItem>,
    val userId: String
)
