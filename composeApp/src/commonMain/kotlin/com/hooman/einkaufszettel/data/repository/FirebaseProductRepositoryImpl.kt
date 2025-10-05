package com.hooman.einkaufszettel.data.repository

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.model.Product
import com.hooman.einkaufszettel.domain.repository.FirebaseProductRepository
import com.hooman.einkaufszettel.domain.source.FirebaseProductDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FirebaseProductRepositoryImpl(
    private val dataSource: FirebaseProductDataSource
): FirebaseProductRepository {
    override fun insertProduct(product: Product): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            dataSource.insertProduct(product)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }

    override fun getAllProductsByUserId(userId: String): Flow<Resource<List<Product>>> = flow {
        emit(Resource.Loading())
        try {
            dataSource.getAllProductsByUserId(userId).collect { products ->
                emit(Resource.Success(products))
                }
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }

    override fun getProductByName(name: String): Flow<Resource<List<Product>>> = flow {
        emit(Resource.Loading())
        try {
            dataSource.getProductByName(name).collect { products ->
                emit(Resource.Success(products))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }

    }

    override fun getProductById(productId: String): Flow<Resource<Product>> = flow {
       emit(Resource.Loading())
        try {
            dataSource.getProductById(productId).collect { product ->
                emit(Resource.Success(product))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }

    override fun deleteProduct(productId: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            dataSource.deleteProduct(productId)
            emit(Resource.Success(Unit))
        }catch (e: Exception){
            emit(Resource.Error(e.message))
        }
    }
}