package com.hooman.einkaufszettel.domain.usecase

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.data.local.entity.SyncStatus
import com.hooman.einkaufszettel.domain.repository.LocalRepository

class UpdateProductSyncDataUseCase(
    private val repository: LocalRepository
) {
    suspend operator fun invoke(productId: String, syncStatus: SyncStatus): Resource<Unit>{
        return repository.updateProductSyncStatus(productId, syncStatus)
    }
}