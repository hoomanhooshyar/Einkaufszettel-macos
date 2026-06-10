package com.hooman.einkaufszettel.domain.model

data class ShoppingDetails(
    val productId: String,
    val productImage: String,
    val productName: String,
    val productPrice: Double,
    val shoppingItemId: String,
    val isChecked: Boolean,
    val itemCount: Int?,
)
