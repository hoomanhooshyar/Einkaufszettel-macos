package com.hooman.einkaufszettel.domain.usecase

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.repository.LocalRepository


class UpdateShoppingItemDiscountInLocalUseCase(
    private val repository: LocalRepository
) {
    suspend operator fun invoke(shoppingItemId: String, discount: Float): Resource<Unit> {
        return repository.updateShoppingItemDiscount(shoppingItemId, discount)
    }

}