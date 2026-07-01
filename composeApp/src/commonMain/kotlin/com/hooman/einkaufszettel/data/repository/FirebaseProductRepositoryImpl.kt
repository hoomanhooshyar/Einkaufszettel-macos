package com.hooman.einkaufszettel.data.repository

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.model.Product
import com.hooman.einkaufszettel.domain.repository.FirebaseProductRepository
import com.hooman.einkaufszettel.domain.source.FirebaseProductDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class FirebaseProductRepositoryImpl(
    private val dataSource: FirebaseProductDataSource
): FirebaseProductRepository {
    override suspend fun insertProduct(product: Product): Resource<Unit>{

        return try {
            dataSource.insertProduct(product)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override fun getAllProductsByUserId(userId: String): Flow<Resource<List<Product>>> {
        return dataSource.getAllProductsByUserId(userId)
            .map { products ->
                Resource.Success(products) as Resource<List<Product>>
            }
            .onStart {
                emit(Resource.Loading())
            }
            .catch { e ->
                emit(Resource.Error(e.message))
            }
    }

    override fun getProductByName(name: String): Flow<Resource<List<Product>>> {
        return dataSource.getProductByName(name)
            .map { products ->
                Resource.Success(products) as Resource<List<Product>>
            }
            .onStart {
                emit(Resource.Loading())
            }
            .catch { e ->
                emit(Resource.Error(e.message))
            }

    }

    override fun getProductIcons(): Flow<Resource<List<String>>>{
        return dataSource.getProductIcon()
            .map { icons ->
                Resource.Success(icons) as Resource<List<String>>
            }
            .onStart {
                emit(Resource.Loading())
            }
            .catch { e ->
                emit(Resource.Error(e.message))
            }
    }

    override fun getProductById(productId: String): Flow<Resource<Product>> {
       return dataSource.getProductById(productId)
           .map { product ->
               Resource.Success(product) as Resource<Product>
           }
           .onStart {
               emit(Resource.Loading())
           }
           .catch { e ->
               emit(Resource.Error(e.message))
           }
    }

    override suspend fun deleteProduct(productId: String):Resource<Unit> {
        return try {
            dataSource.deleteProduct(productId)
            Resource.Success(Unit)
        }catch (e: Exception){
            Resource.Error(e.message)
        }
    }
}