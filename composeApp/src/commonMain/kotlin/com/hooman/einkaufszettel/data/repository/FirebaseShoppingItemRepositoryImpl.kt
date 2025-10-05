package com.hooman.einkaufszettel.data.repository

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.model.ShoppingItem
import com.hooman.einkaufszettel.domain.repository.FirebaseShoppingItemRepository
import com.hooman.einkaufszettel.domain.source.FirebaseShoppingItemDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FirebaseShoppingItemRepositoryImpl(
    private val dataSource: FirebaseShoppingItemDataSource
): FirebaseShoppingItemRepository {
    override fun insertShoppingItem(shoppingItem: ShoppingItem): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            dataSource.insertItem(shoppingItem)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }

    override fun getAllShoppingItemsByUserId(userId: String): Flow<Resource<List<ShoppingItem>>> = flow {
        emit(Resource.Loading())
        try {
            dataSource.getAllItemsByUserId(userId).collect { items ->
                emit(Resource.Success(items))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }

    override fun deleteShoppingItem(
        billId: String,
        itemId: String
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            dataSource.deleteShoppingItem(billId, itemId)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }

    override fun getShoppingItemByBillId(billId: String): Flow<Resource<List<ShoppingItem>>> = flow {
       emit(Resource.Loading())
        try {
            dataSource.getShoppingItemByBillId(billId).collect { items ->
                emit(Resource.Success(items))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }
}