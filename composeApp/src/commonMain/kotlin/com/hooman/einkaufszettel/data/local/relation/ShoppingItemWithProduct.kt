package com.hooman.einkaufszettel.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.hooman.einkaufszettel.data.local.entity.ProductEntity
import com.hooman.einkaufszettel.data.local.entity.ShoppingItemEntity

data class ShoppingItemWithProduct(
    @Embedded val item: ShoppingItemEntity,
    @Relation(
        parentColumn = "productId",
        entityColumn = "id"
    )
    val product: ProductEntity
)
