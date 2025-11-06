package com.hooman.einkaufszettel.domain.usecase

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.model.Product
import com.hooman.einkaufszettel.domain.repository.FirebaseProductRepository
import com.hooman.einkaufszettel.domain.repository.LocalRepository
import kotlinx.coroutines.flow.Flow

class GetProductByIdFromLocalUseCase(
    private val repository: LocalRepository
) {
    suspend operator fun invoke(productId: String): Flow<Resource<Product>>{
        return repository.getProductById(productId)
    }
}