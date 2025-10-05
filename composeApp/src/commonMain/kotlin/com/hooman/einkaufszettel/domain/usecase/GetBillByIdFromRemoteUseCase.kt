package com.hooman.einkaufszettel.domain.usecase

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.model.Bill
import com.hooman.einkaufszettel.domain.repository.FirebaseBillRepository
import kotlinx.coroutines.flow.Flow

class GetBillByIdFromRemoteUseCase(
    private val repository: FirebaseBillRepository
) {
    suspend operator fun invoke(billId: String): Flow<Resource<Bill>>{
        return repository.getBillById(billId)
    }
}