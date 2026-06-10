package com.hooman.einkaufszettel.domain.repository

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.model.ShoppingItem
import kotlinx.coroutines.flow.Flow

interface FirebaseShoppingItemRepository {
    suspend fun insertShoppingItem(shoppingItem: ShoppingItem): Resource<Unit>
    fun getAllShoppingItemsByUserId(userId: String): Flow<Resource<List<ShoppingItem>>>
    suspend fun deleteShoppingItem(billId: String, shoppingItemId: String):Resource<Unit>
    fun getShoppingItemByBillId(billId: String): Flow<Resource<List<ShoppingItem>>>
    suspend fun updateShoppingItemCheckStatus(billId: String, itemId: String, isChecked: Boolean):Resource<Unit>
    suspend fun updateShoppingItemCount(billId: String, itemId: String, itemCount: Int): Resource<Unit>
}