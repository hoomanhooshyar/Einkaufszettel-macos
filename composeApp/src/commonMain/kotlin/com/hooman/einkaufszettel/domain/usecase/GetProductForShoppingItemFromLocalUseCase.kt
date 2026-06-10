package com.hooman.einkaufszettel.domain.usecase

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.model.Product
import com.hooman.einkaufszettel.domain.model.ShoppingDetails
import com.hooman.einkaufszettel.domain.repository.LocalRepository
import kotlinx.coroutines.flow.Flow

class GetProductForShoppingItemFromLocalUseCase(
    private val repository: LocalRepository
) {
    suspend operator fun invoke(billId: String): Flow<Resource<List<ShoppingDetails>>> {
        return repository.getProductsForShoppingItem(billId)
    }
}