package com.hooman.einkaufszettel.domain.usecase

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.model.Bill
import com.hooman.einkaufszettel.domain.repository.LocalRepository
import kotlinx.coroutines.flow.Flow

class GetBillByIdFromLocalUseCase(
    private val repository: LocalRepository
) {
    suspend operator fun invoke(billId: String): Flow<Resource<Bill>>{
        return repository.getBillById(billId)
    }

}