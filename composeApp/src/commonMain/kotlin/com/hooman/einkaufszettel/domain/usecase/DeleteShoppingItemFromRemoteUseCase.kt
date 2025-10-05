package com.hooman.einkaufszettel.domain.usecase

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.repository.FirebaseShoppingItemRepository
import kotlinx.coroutines.flow.Flow

class DeleteShoppingItemFromRemoteUseCase(
    private val repository: FirebaseShoppingItemRepository
) {
    suspend operator fun invoke(shoppingItemId: String, billId: String): Flow<Resource<Unit>> {
        return repository.deleteShoppingItem(
            billId = billId,
            itemId = shoppingItemId
        )
    }
}