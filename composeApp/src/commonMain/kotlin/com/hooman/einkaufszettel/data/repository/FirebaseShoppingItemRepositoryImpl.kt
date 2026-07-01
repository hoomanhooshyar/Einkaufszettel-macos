package com.hooman.einkaufszettel.data.repository

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.model.ShoppingItem
import com.hooman.einkaufszettel.domain.repository.FirebaseShoppingItemRepository
import com.hooman.einkaufszettel.domain.source.FirebaseShoppingItemDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

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

    override fun getAllShoppingItemsByUserId(userId: String): Flow<Resource<List<ShoppingItem>>> {
        return dataSource.getAllItemsByUserId(userId)
            .map { items ->
                Resource.Success(items) as Resource<List<ShoppingItem>>
            }
            .onStart {
                emit(Resource.Loading())
            }
            .catch { e ->
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

    override suspend fun deleteShoppingItemByProductAndBill(
        billId: String,
        productId: String
    ): Resource<Unit> {
        return try {
            dataSource.deleteShoppingItemByProductAndBill(billId = billId, productId = productId)
            Resource.Success(Unit)
        }catch (e: Exception){
            Resource.Error(e.message)
        }
    }

    override fun getShoppingItemByBillId(billId: String): Flow<Resource<List<ShoppingItem>>> {
       return dataSource.getShoppingItemByBillId(billId)
           .map { items ->
               Resource.Success(items) as Resource<List<ShoppingItem>>
           }
           .onStart {
               emit(Resource.Loading())
           }
           .catch { e ->
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
        productId: String,
        itemCount: Int
    ): Resource<Unit> {
        return try {
            dataSource.updateShoppingItemCount(billId, productId, itemCount)
            Resource.Success(Unit)
        }catch (e: Exception){
            Resource.Error(e.message)
        }
    }

    override suspend fun updateShoppingItemDiscount(
        billId: String,
        productId: String,
        discount: Float
    ): Resource<Unit> {
        return try {
            dataSource.updateShoppingItemDiscount(billId, productId, discount)
            Resource.Success(Unit)
        }catch (e: Exception){
            Resource.Error(e.message)
        }
    }
}