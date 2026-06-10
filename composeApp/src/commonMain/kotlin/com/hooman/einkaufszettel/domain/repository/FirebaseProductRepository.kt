package com.hooman.einkaufszettel.domain.repository

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface FirebaseProductRepository {

    suspend fun insertProduct(product: Product):Resource<Unit>
    fun getAllProductsByUserId(userId: String): Flow<Resource<List<Product>>>
    fun getProductByName(name: String): Flow<Resource<List<Product>>>
    fun getProductIcons(): Flow<Resource<List<String>>>
    fun getProductById(productId: String): Flow<Resource<Product>>
    suspend fun deleteProduct(productId: String): Resource<Unit>
}