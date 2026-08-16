package com.hooman.einkaufszettel.data.mapper

import com.hooman.einkaufszettel.data.local.entity.ProductEntity
import com.hooman.einkaufszettel.domain.model.Product

fun Product.toProductEntity(): ProductEntity {
    return ProductEntity(
        id = id,
        name = name,
        image = image,
        price = price,
        syncStatus = syncStatus
    )
}

fun ProductEntity.toProduct(): Product {
    return Product(
        id = id,
        name = name,
        image = image,
        price = price,
        syncStatus = syncStatus,
        userId = ""
    )
}