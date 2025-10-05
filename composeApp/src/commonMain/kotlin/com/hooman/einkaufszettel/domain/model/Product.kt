package com.hooman.einkaufszettel.domain.model

data class Product(
    val id: String,
    val name: String,
    val image: String?,
    val price: Double,
    val userId: String
)
