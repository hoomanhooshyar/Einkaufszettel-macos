package com.hooman.einkaufszettel.domain.usecase

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.repository.FirebaseShoppingItemRepository
import kotlinx.coroutines.flow.Flow

class UpdateShoppingItemCheckStatusInRemoteUseCase(
    private val repository: FirebaseShoppingItemRepository
) {
    suspend operator fun invoke(billId: String, itemId: String, isChecked: Boolean):Resource<Unit> {
        return repository.updateShoppingItemCheckStatus(billId, itemId, isChecked)
    }
}