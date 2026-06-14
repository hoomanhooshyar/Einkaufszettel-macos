package com.hooman.einkaufszettel.domain.usecase

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.repository.LocalRepository

class DeleteShoppingItemByProductAndBillFromLocalUseCase(
    private val repository: LocalRepository
) {
    suspend operator fun invoke(billId: String, productId: String): Resource<Unit>{
        return repository.deleteShoppingItemByProductAndBill(billId, productId)
    }
}