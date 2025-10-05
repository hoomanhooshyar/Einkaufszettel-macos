package com.hooman.einkaufszettel.domain.usecase

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.model.Product
import com.hooman.einkaufszettel.domain.repository.LocalRepository
import kotlinx.coroutines.flow.Flow

class DeleteProductFromLocalUseCase(
    private val repository: LocalRepository

) {
    suspend operator fun invoke(product: Product): Flow<Resource<Unit>> {
        return repository.deleteProduct(product)
    }
}