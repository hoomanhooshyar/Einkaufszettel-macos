package com.hooman.einkaufszettel.data.remote.dto

import com.hooman.einkaufszettel.domain.model.ShoppingItem
import kotlinx.serialization.Serializable

@Serializable
data class ShoppingItemDto(
    val billId: String = "",
    val productId: String = "",
    val itemCount: Int = 0,
    val discount: Float = 0f,
    val productName: String = "",
    val productPrice: Double = 0.0,
    val productImage: String? = null,
    val userId: String = "",
    val isChecked: Boolean = false
){
    fun toDomain(id: String): ShoppingItem = ShoppingItem(
        id = id,
        billId = billId,
        productId = productId,
        itemCount = itemCount,
        productName = productName,
        productPrice = productPrice,
        productImage = productImage,
        discount = discount,
        isChecked = isChecked,
        userId = userId
    )

    companion object {
        fun fromDomain(item: ShoppingItem): ShoppingItemDto = ShoppingItemDto(
            billId = item.billId,
            productId = item.productId,
            itemCount = item.itemCount,
            productName = item.productName,
            productPrice = item.productPrice,
            discount = item.discount,
            productImage = item.productImage,
            isChecked = item.isChecked,
            userId = item.userId
        )
    }
}
