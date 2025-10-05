package com.hooman.einkaufszettel.domain.usecase

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.model.Product
import com.hooman.einkaufszettel.domain.repository.FirebaseProductRepository
import kotlinx.coroutines.flow.Flow

class GetAllProductsByUserIdFromRemoteUseCase(
    private val repository: FirebaseProductRepository
) {
    suspend operator fun invoke(userId: String): Flow<Resource<List<Product?>>>{
        return repository.getAllProductsByUserId(userId)
    }

}