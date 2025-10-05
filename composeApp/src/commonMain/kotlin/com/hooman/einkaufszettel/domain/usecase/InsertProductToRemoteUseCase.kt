package com.hooman.einkaufszettel.domain.usecase

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.model.Product
import com.hooman.einkaufszettel.domain.repository.FirebaseProductRepository
import kotlinx.coroutines.flow.Flow

class InsertProductToRemoteUseCase(
    private val repository: FirebaseProductRepository
) {
    suspend operator fun invoke(product: Product): Flow<Resource<Unit>> {
        return repository.insertProduct(product)
    }
}