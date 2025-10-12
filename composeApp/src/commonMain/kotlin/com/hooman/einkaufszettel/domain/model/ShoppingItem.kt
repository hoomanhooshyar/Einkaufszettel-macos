package com.hooman.einkaufszettel.domain.model

data class ShoppingItem(
    val id: String,
    val billId: String,
    val productId: String,
    val itemCount: Int,
    val productName: String,
    val productPrice: Double,
    val productImage: String?,
    val isChecked: Boolean,
    val userId: String
)
