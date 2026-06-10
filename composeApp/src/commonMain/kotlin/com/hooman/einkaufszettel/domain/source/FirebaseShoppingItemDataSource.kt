package com.hooman.einkaufszettel.domain.source

import com.hooman.einkaufszettel.domain.model.ShoppingItem
import kotlinx.coroutines.flow.Flow

interface FirebaseShoppingItemDataSource {
    suspend fun insertItem(shoppingItem: ShoppingItem)
    fun getAllItemsByUserId(userId:String): Flow<List<ShoppingItem>>
    suspend fun deleteShoppingItem(billId:String,productId:String)
    fun getShoppingItemByBillId(billId: String): Flow<List<ShoppingItem>>
    suspend fun updateShoppingItemCheckStatus(billId: String, itemId: String, isChecked: Boolean)
    suspend fun updateShoppingItemCount(billId: String, itemId: String, itemCount: Int)
}