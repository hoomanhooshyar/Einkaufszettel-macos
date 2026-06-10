package com.hooman.einkaufszettel.domain.usecase

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.model.Bill
import com.hooman.einkaufszettel.domain.repository.FirebaseBillRepository
import kotlinx.coroutines.flow.Flow

class InsertBillToRemoteUseCase(
    private val repository: FirebaseBillRepository
) {
    suspend operator fun invoke(bill: Bill): Resource<Unit> {
        return repository.insertBill(bill)
    }
}