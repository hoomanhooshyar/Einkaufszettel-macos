package com.hooman.einkaufszettel.data.repositrory

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.model.Product
import com.hooman.einkaufszettel.domain.repository.FirebaseProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeFirebaseProductRepository: FirebaseProductRepository {

    var hasInternet = true
    var shouldErrorThrow = false

    val remoteProducts = MutableStateFlow<List<Product>>(emptyList())

    val productIcons = MutableStateFlow(listOf("Icon 1", "Icon 2", "Icon 3"))

    override suspend fun insertProduct(product: Product): Resource<Unit> {
        if(!hasInternet) return Resource.Error("No Internet Connection")
        if(shouldErrorThrow) return Resource.Error("Firebase Server Error")

        remoteProducts.update { currentProducts ->
            currentProducts.filterNot { it.id == product.id } + product
        }
        return Resource.Success(Unit)
    }

    override fun getAllProductsByUserId(userId: String): Flow<Resource<List<Product>>> {
        return remoteProducts.map { products ->
            if(!hasInternet){
                Resource.Error("No Internet Connection")
            }else if(shouldErrorThrow){
                Resource.Error("Firebase Server Error")
            }else{
                val userProducts = products.filter { it.userId == userId }
                Resource.Success(userProducts)
            }
        }
    }

    override fun getProductByName(name: String): Flow<Resource<List<Product>>> {
        return remoteProducts.map { products ->
            if(!hasInternet){
                Resource.Error("No Internet Connection")
            }else if(shouldErrorThrow){
                Resource.Error("Firebase Server  has Error")
            }else{
                val product = products.filter { it.name == name }
                Resource.Success(product)
            }
        }
    }

    override fun getProductIcons(): Flow<Resource<List<String>>> {
        return productIcons.map { icons ->
            if(!hasInternet){
                Resource.Error("No Internet Connection")
            }else if(shouldErrorThrow){
                Resource.Error("Firebase Server has Error")
            }else{
                Resource.Success(icons)
            }
        }
    }

    override fun getProductById(productId: String): Flow<Resource<Product>> {
        return remoteProducts.map { products ->
            if(!hasInternet){
                Resource.Error("No Internet Connection")
            }else if(shouldErrorThrow){
                Resource.Error("Firebase Server  has Error")
            }else{
                val product = products.filter { it.id == productId }
                Resource.Success(product.first())
            }
        }
    }

    override suspend fun deleteProduct(productId: String): Resource<Unit> {
        if(!hasInternet) return Resource.Error("No Internet Connection")
        if(shouldErrorThrow) return Resource.Error("Firebase Server has Error")
        remoteProducts.update { currentProducts ->
            currentProducts.filterNot { it.id == productId }
        }
        return Resource.Success(Unit)
    }
}