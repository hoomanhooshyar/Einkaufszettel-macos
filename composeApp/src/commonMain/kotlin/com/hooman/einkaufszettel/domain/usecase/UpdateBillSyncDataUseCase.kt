package com.hooman.einkaufszettel.domain.usecase

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.data.local.entity.SyncStatus
import com.hooman.einkaufszettel.domain.repository.LocalRepository

open class UpdateBillSyncDataUseCase(
    private val repository: LocalRepository
) {
    open suspend operator fun invoke(billId: String, syncStatus: SyncStatus): Resource<Unit>{
        return repository.updateBillSyncStatus(billId, syncStatus)
    }
}