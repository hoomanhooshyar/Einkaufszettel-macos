package com.hooman.einkaufszettel.domain.usecase

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.repository.LocalRepository
import kotlinx.coroutines.flow.Flow

class UpdateShoppingItemCheckStatusInLocalUseCase(
    private val repository: LocalRepository
) {
    suspend operator fun invoke(shoppingItemId: String, isChecked: Boolean): Resource<Unit>{
        return repository.updateShoppingItemCheckStatus(shoppingItemId, isChecked)
    }
}