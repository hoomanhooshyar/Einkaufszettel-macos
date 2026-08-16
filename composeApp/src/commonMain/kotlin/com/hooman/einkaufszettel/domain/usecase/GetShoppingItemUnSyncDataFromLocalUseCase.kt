package com.hooman.einkaufszettel.domain.usecase

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.model.ShoppingDetails
import com.hooman.einkaufszettel.domain.model.ShoppingItem
import com.hooman.einkaufszettel.domain.repository.LocalRepository
import kotlinx.coroutines.flow.Flow

class GetShoppingItemUnSyncDataFromLocalUseCase(
    private val repository: LocalRepository
) {
    operator fun invoke(): Flow<Resource<List<ShoppingDetails>>>{
        return repository.getShoppingItemUnSyncData()
    }
}