package com.hooman.einkaufszettel.domain.usecase

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.repository.FirebaseShoppingItemRepository
import kotlinx.coroutines.flow.Flow

class UpdateShoppingItemCountInRemoteUseCase(
    private val repository: FirebaseShoppingItemRepository
) {
    suspend operator fun invoke(billId: String, itemId: String, itemCount: Int):Resource<Unit>{
        return repository.updateShoppingItemCount(billId, itemId, itemCount)
    }
}