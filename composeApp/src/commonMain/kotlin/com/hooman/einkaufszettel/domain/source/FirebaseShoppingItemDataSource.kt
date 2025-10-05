package com.hooman.einkaufszettel.domain.source

import com.hooman.einkaufszettel.domain.model.ShoppingItem
import kotlinx.coroutines.flow.Flow

interface FirebaseShoppingItemDataSource {
    suspend fun insertItem(shoppingItem: ShoppingItem)
    fun getAllItemsByUserId(userId:String): Flow<List<ShoppingItem>>
    suspend fun deleteShoppingItem(billId:String,itemId:String)
    fun getShoppingItemByBillId(billId: String): Flow<List<ShoppingItem>>
}