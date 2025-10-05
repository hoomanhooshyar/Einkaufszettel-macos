package com.hooman.einkaufszettel.data.remote.dto

import com.hooman.einkaufszettel.domain.model.Product
import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    val name: String = "",
    val image: String? = null,
    val price: Double = 0.0,
    val userId: String = ""
){
    fun toDomain(id: String): Product = Product(
        id = id,
        name = name,
        image = image,
        price = price,
        userId = userId
    )

    companion object {
        fun fromDomain(product: Product): ProductDto = ProductDto(
            name = product.name,
            image = product.image,
            price = product.price,
            userId = product.userId
        )
    }
}
