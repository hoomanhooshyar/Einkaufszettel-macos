package com.hooman.einkaufszettel.domain.usecase

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.model.ShoppingItem
import com.hooman.einkaufszettel.domain.repository.LocalRepository
import kotlinx.coroutines.flow.Flow

class GetShoppingItemByBillIdFromLocalUseCase(
    private val repository: LocalRepository
) {
    suspend operator fun invoke(billId: String): Flow<Resource<List<ShoppingItem>>>{
        return repository.getAllShoppingItemsByBillId(billId)
    }
}