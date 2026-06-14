package com.hooman.einkaufszettel.domain.usecase

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.repository.FirebaseShoppingItemRepository


class DeleteShoppingItemByProductAndBillFromRemoteUseCase(
    private val repository: FirebaseShoppingItemRepository
) {
    suspend operator fun invoke(billId: String, productId: String): Resource<Unit> {
        return repository.deleteShoppingItemByProductAndBill(billId = billId, productId = productId)
    }
}