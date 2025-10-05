package com.hooman.einkaufszettel.domain.repository

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.model.ShoppingItem
import kotlinx.coroutines.flow.Flow

interface FirebaseShoppingItemRepository {
    fun insertShoppingItem(shoppingItem: ShoppingItem): Flow<Resource<Unit>>
    fun getAllShoppingItemsByUserId(userId: String): Flow<Resource<List<ShoppingItem>>>
    fun deleteShoppingItem(billId: String, itemId: String): Flow<Resource<Unit>>
    fun getShoppingItemByBillId(billId: String): Flow<Resource<List<ShoppingItem>>>
}