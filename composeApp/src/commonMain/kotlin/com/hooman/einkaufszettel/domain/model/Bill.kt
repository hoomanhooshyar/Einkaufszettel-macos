package com.hooman.einkaufszettel.domain.model

import com.hooman.einkaufszettel.core.util.KeepForFirebase
import com.hooman.einkaufszettel.data.local.entity.SyncStatus
import kotlinx.datetime.Instant

@KeepForFirebase
data class Bill(
    val id: String,
    val billDate: Instant,
    val items: List<ShoppingItem>,
    val name: String,
    val userId: String,
    val type: PurchaseType,
    val syncStatus: SyncStatus
)
