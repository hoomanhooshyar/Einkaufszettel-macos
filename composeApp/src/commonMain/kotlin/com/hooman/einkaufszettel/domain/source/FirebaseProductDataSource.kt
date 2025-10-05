package com.hooman.einkaufszettel.domain.source

import com.hooman.einkaufszettel.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface FirebaseProductDataSource {
    fun getAllProductsByUserId(userId: String): Flow<List<Product>>
    fun getProductByName(name: String): Flow<List<Product>>
    fun getProductById(productId: String): Flow<Product>
    suspend fun insertProduct(product: Product)
    suspend fun deleteProduct(productId: String)
}