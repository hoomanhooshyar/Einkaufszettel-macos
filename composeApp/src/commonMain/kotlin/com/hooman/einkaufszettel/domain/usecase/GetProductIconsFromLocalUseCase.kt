package com.hooman.einkaufszettel.domain.usecase

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.repository.LocalAssetsRepository
import kotlinx.coroutines.flow.Flow

class GetProductIconsFromLocalUseCase(
    private val repository: LocalAssetsRepository
) {
    suspend operator fun invoke(): Flow<Resource<List<String>>> {
        return repository.getProductIcons()
    }
}