package com.hooman.einkaufszettel.domain.model

import com.hooman.einkaufszettel.data.local.entity.SyncStatus

data class ShoppingItem(
    val id: String,
    val billId: String,
    val productId: String,
    val itemCount: Int,
    val discount: Float,
    val productName: String,
    val productPrice: Double,
    val productImage: String?,
    val isChecked: Boolean,
    val userId: String,
    val syncStatus: SyncStatus
)
