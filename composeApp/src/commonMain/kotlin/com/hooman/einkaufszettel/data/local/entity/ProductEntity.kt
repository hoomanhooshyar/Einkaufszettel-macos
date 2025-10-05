package com.hooman.einkaufszettel.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product")
data class ProductEntity(
    @PrimaryKey()
    val id: String,
    val name: String,
    val image: String?,
    val price: Double,
)
