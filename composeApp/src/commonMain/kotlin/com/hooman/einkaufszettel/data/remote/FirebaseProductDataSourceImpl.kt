package com.hooman.einkaufszettel.data.remote

import com.hooman.einkaufszettel.data.remote.dto.ProductDto
import com.hooman.einkaufszettel.domain.model.Product
import com.hooman.einkaufszettel.domain.source.FirebaseProductDataSource
import com.hooman.einkaufszettel.domain.source.FirebaseService
import dev.gitlive.firebase.firestore.where
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FirebaseProductDataSourceImpl(
    private val svc: FirebaseService
): FirebaseProductDataSource {
    override  fun getAllProductsByUserId(userId: String): Flow<List<Product>> {
        return svc.productsCol()
            .where { "userId" equalTo userId }
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { doc ->
                    doc.data<ProductDto>().toDomain(doc.id)
                }
            }
    }



    override  fun getProductByName(name: String): Flow<List<Product>> {
        return svc.productsCol()
            .where { "name" equalTo name }
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { doc ->
                    doc.data<ProductDto>().toDomain(doc.id)
                }
            }
    }



    override  fun getProductById(productId: String): Flow<Product> {
        return svc.productsCol()
            .document(productId)
            .snapshots
            .map { doc ->
                doc.data<ProductDto>().toDomain(doc.id)
            }
    }



    override suspend fun insertProduct(product: Product) {
        svc.io {
            svc.productsCol().add(ProductDto.fromDomain(product))
        }
    }

    override suspend fun deleteProduct(productId: String) {
        svc.io {
            svc.productsCol().document(productId).delete()
        }

    }
}