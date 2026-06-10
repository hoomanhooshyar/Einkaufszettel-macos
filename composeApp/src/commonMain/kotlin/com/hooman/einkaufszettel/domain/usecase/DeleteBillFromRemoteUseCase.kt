package com.hooman.einkaufszettel.domain.usecase

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.repository.FirebaseBillRepository
import kotlinx.coroutines.flow.Flow

class DeleteBillFromRemoteUseCase(
    private val repository: FirebaseBillRepository

) {
    suspend operator fun invoke(billId: String):Resource<Unit> {
        return repository.deleteBill(billId)
    }
}