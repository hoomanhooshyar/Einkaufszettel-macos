package com.hooman.einkaufszettel.domain.usecase

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.repository.LocalRepository
import kotlinx.coroutines.flow.Flow

class UpdateShoppingItemCountInLocalUseCase(
    private val repository: LocalRepository
) {
    suspend operator fun invoke(shoppingItemId: String, itemCount: Int): Resource<Unit> {
        return repository.updateSHoppingItemCount(shoppingItemId, itemCount)
    }
}