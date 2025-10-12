package com.hooman.einkaufszettel.domain.model

import kotlinx.datetime.Instant

data class Bill(
    val id: String,
    val billDate: Instant,
    val items: List<ShoppingItem>,
    val name: String,
    val userId: String,
    val type: PurchaseType
)
