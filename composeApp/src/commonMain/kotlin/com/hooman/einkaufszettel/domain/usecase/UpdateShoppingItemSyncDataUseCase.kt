package com.hooman.einkaufszettel.domain.usecase

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.data.local.entity.SyncStatus
import com.hooman.einkaufszettel.domain.repository.LocalRepository

class UpdateShoppingItemSyncDataUseCase(
    private val repository: LocalRepository
) {
    operator suspend fun invoke(itemId: String, syncStatus: SyncStatus): Resource<Unit>{
        return repository.updateShoppingItemSyncStatus(itemId, syncStatus)
    }
}