package com.hooman.einkaufszettel.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.hooman.einkaufszettel.data.local.entity.BillEntity
import com.hooman.einkaufszettel.data.local.entity.ShoppingItemEntity

data class BillWithItemsAndProducts(
    @Embedded val bill: BillEntity,
    @Relation(
        entity = ShoppingItemEntity::class,
        parentColumn = "id",
        entityColumn = "billId"
    )
    val items: List<ShoppingItemWithProduct>,

    )
