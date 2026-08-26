package com.hooman.einkaufszettel.domain.model

import com.hooman.einkaufszettel.core.util.KeepForFirebase
import com.hooman.einkaufszettel.data.local.entity.SyncStatus

@KeepForFirebase
data class Product(
    val id: String,
    val name: String,
    val image: String?,
    val price: Double,
    val userId: String,
    val syncStatus: SyncStatus
)
