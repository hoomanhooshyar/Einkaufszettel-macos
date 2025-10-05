package com.hooman.einkaufszettel.domain.repository

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface FirebaseProductRepository {

    fun insertProduct(product: Product): Flow<Resource<Unit>>
    fun getAllProductsByUserId(userId: String): Flow<Resource<List<Product>>>
    fun getProductByName(name: String): Flow<Resource<List<Product>>>
    fun getProductById(productId: String): Flow<Resource<Product>>
    fun deleteProduct(productId: String): Flow<Resource<Unit>>
}