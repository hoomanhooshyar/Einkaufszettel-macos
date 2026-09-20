package com.hooman.einkaufszettel.data.mapper

import com.hooman.einkaufszettel.data.local.entity.ShoppingItemEntity
import com.hooman.einkaufszettel.domain.model.ShoppingDetails
import com.hooman.einkaufszettel.domain.model.ShoppingItem


fun ShoppingItem.toEntity(): ShoppingItemEntity {
    return ShoppingItemEntity(
        id = id,
        billId = billId,
        productId = productId,
        itemCount = itemCount,
        isChecked = isChecked,
        discount = discount,
        syncStatus = syncStatus,
        userId = userId
    )
}




fun ShoppingItemEntity.toShoppingItem(): ShoppingItem {
    return ShoppingItem(
        id = id,
        billId = billId,
        productId = productId,
        itemCount = itemCount,
        discount = discount,
        productName = "",
        productPrice = 0.0,
        productImage = "",
        isChecked = isChecked,
        syncStatus = syncStatus,
        userId = userId
    )
}

fun ShoppingDetails.toShoppingItem(currentUserId: String): ShoppingItem{
    require(userId == currentUserId && userId.isNotBlank()) { "Account ownership mismatch" }
    return ShoppingItem(
        id = shoppingItemId,
        billId = billId,
        productId = productId,
        itemCount = itemCount ?: 0,
        discount = discount,
        productName = productName,
        productPrice = productPrice,
        productImage = productImage,
        isChecked = isChecked,
        syncStatus = syncStatus,
        userId = userId
    )
}