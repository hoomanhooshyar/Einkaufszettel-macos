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
    override suspend fun insertShoppingItem(shoppingItem: ShoppingItem):Resource<Unit> {
        return try {
            dataSource.insertItem(shoppingItem)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message)
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

    override suspend fun deleteShoppingItem(
        billId: String,
        shoppingItemId: String
    ): Resource<Unit> {
        return try {
            dataSource.deleteShoppingItem(billId, shoppingItemId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message)
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

    override suspend fun updateShoppingItemCheckStatus(
        billId: String,
        itemId: String,
        isChecked: Boolean
    ):Resource<Unit> {
        return try {
            dataSource.updateShoppingItemCheckStatus(billId, itemId, isChecked)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun updateShoppingItemCount(
        billId: String,
        itemId: String,
        itemCount: Int
    ): Resource<Unit> {
        return try {
            dataSource.updateShoppingItemCount(billId, itemId, itemCount)
            Resource.Success(Unit)
        }catch (e: Exception){
            Resource.Error(e.message)
        }
    }
}