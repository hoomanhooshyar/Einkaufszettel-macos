package com.hooman.einkaufszettel.domain.source

import com.hooman.einkaufszettel.domain.model.ShoppingItem
import kotlinx.coroutines.flow.Flow

interface FirebaseShoppingItemDataSource {
    suspend fun insertItem(shoppingItem: ShoppingItem)
    fun getAllItemsByUserId(userId:String): Flow<List<ShoppingItem>>
    suspend fun deleteShoppingItem(billId:String,shoppingItemId:String)
    suspend fun deleteShoppingItemByProductAndBill(billId: String, productId: String)
    fun getShoppingItemByBillId(billId: String): Flow<List<ShoppingItem>>
    suspend fun updateShoppingItemCheckStatus(billId: String, itemId: String, isChecked: Boolean)
    suspend fun updateShoppingItemCount(billId: String, productId: String, itemCount: Int)
    suspend fun updateShoppingItemDiscount(billId: String, productId: String, discount: Float)
}