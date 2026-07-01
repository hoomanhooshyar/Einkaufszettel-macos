package com.hooman.einkaufszettel.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "shopping_items",
    foreignKeys = [
        ForeignKey(
            entity = BillEntity::class,
            parentColumns = ["id"],
            childColumns = ["billId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("billId"), Index("productId")]
)
data class ShoppingItemEntity(
    @PrimaryKey()
    val id: String,
    val billId: String,
    val productId: String,
    val itemCount: Int,
    val discount: Float,
    val isChecked: Boolean
)
