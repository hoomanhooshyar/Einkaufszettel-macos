package com.hooman.einkaufszettel.domain.usecase

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.model.ShoppingItem
import com.hooman.einkaufszettel.domain.repository.LocalRepository
import kotlinx.coroutines.flow.Flow

class InsertShoppingItemToLocalUseCase(
    private val repository: LocalRepository
) {
    suspend operator fun invoke(shoppingItem: ShoppingItem,billId: String): Flow<Resource<Unit>> {
        return repository.insertShoppingItem(
            shoppingItem = shoppingItem,
            billId = billId
        )
    }
}