package com.hooman.einkaufszettel.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant

@Entity(tableName = "bill")
data class BillEntity(
    @PrimaryKey()
    val id: String,
    val billDate: Instant,
    val name: String,
    val type: String,
    val syncStatus: SyncStatus
)
