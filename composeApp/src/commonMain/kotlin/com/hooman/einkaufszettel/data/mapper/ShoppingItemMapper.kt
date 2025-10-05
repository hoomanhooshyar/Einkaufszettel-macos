package com.hooman.einkaufszettel.data.mapper

import com.hooman.einkaufszettel.data.local.entity.ShoppingItemEntity
import com.hooman.einkaufszettel.domain.model.ShoppingItem


fun ShoppingItem.toEntity(): ShoppingItemEntity {
    return ShoppingItemEntity(
        id = id,
        billId = billId,
        productId = productId.toLong(),
        itemCount = itemCount
    )
}




fun ShoppingItemEntity.toShoppingItem(): ShoppingItem {
    return ShoppingItem(
        id = id,
        billId = billId,
        productId = productId.toString(),
        itemCount = itemCount,
        productName = "",
        productPrice = 0.0,
        productImage = "",
        userId = ""
    )
}