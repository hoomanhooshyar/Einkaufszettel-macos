package com.hooman.einkaufszettel.domain.usecase

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.model.Product
import com.hooman.einkaufszettel.domain.repository.LocalRepository
import kotlinx.coroutines.flow.Flow

class GetProductByNameFromLocalUseCase(
    private val repository: LocalRepository
) {
    suspend operator fun invoke(name: String): Flow<Resource<List<Product>>> {
        return repository.getProductByName(name)
    }
}