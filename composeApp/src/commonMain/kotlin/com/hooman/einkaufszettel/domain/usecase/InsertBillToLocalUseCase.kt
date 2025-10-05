package com.hooman.einkaufszettel.domain.usecase

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.model.Bill
import com.hooman.einkaufszettel.domain.repository.LocalRepository
import kotlinx.coroutines.flow.Flow

class InsertBillToLocalUseCase(
    private val repository: LocalRepository
) {
    suspend operator fun invoke(bill: Bill): Flow<Resource<Unit>> {
        return repository.insertBill(bill)
    }
}