package com.hooman.einkaufszettel.data.remote.dto

import com.hooman.einkaufszettel.data.local.entity.SyncStatus
import com.hooman.einkaufszettel.domain.model.Product
import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    val name: String = "",
    val image: String? = null,
    val price: Double = 0.0,
    val userId: String = "",
    val syncStatus: SyncStatus
){
    fun toDomain(id: String): Product = Product(
        id = id,
        name = name,
        image = image,
        price = price,
        userId = userId,
        syncStatus = syncStatus
    )

    companion object {
        fun fromDomain(product: Product, status: SyncStatus): ProductDto = ProductDto(
            name = product.name,
            image = product.image,
            price = product.price,
            userId = product.userId,
            syncStatus = status
        )
    }
}
