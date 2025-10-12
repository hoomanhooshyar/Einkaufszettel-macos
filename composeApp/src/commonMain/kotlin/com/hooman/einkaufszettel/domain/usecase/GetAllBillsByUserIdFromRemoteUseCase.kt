package com.hooman.einkaufszettel.domain.usecase

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.model.Bill
import com.hooman.einkaufszettel.domain.repository.FirebaseBillRepository
import kotlinx.coroutines.flow.Flow

class GetAllBillsByUserIdFromRemoteUseCase(
    private val repository: FirebaseBillRepository
) {
    suspend operator fun invoke(userId: String): Flow<Resource<List<Bill>>>{
        return repository.getAllBillsByUserId(userId)
    }

}