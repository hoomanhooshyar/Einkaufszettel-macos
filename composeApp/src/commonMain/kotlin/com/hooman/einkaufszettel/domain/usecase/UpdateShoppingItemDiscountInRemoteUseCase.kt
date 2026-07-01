package com.hooman.einkaufszettel.domain.usecase

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.repository.FirebaseShoppingItemRepository

class UpdateShoppingItemDiscountInRemoteUseCase(
    private val repository: FirebaseShoppingItemRepository
) {
    suspend operator fun invoke(billId: String, productId: String, discount: Float): Resource<Unit> {
        return repository.updateShoppingItemDiscount(billId, productId, discount)
    }
}