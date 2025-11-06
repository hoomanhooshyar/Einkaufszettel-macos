package com.hooman.einkaufszettel.domain.repository

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.model.Bill
import com.hooman.einkaufszettel.domain.model.Product
import com.hooman.einkaufszettel.domain.model.ShoppingItem
import kotlinx.coroutines.flow.Flow

interface LocalRepository {
    fun getAllBills(): Flow<Resource<List<Bill>>>
    fun getBillById(billId: String): Flow<Resource<Bill>>
    fun insertBill(bill: Bill): Flow<Resource<Unit>>
    fun deleteBill(bill: Bill): Flow<Resource<Unit>>
    fun insertShoppingItem(shoppingItem: ShoppingItem,billId: String): Flow<Resource<Unit>>
    fun deleteShoppingItem(shoppingItem: ShoppingItem,billId: String): Flow<Resource<Unit>>
    fun getAllProducts(): Flow<Resource<List<Product>>>
    fun getProductByName(name: String): Flow<Resource<List<Product>>>
    fun getProductById(productId: String): Flow<Resource<Product>>
    fun insertProduct(product: Product): Flow<Resource<Unit>>
    fun deleteProduct(product: Product): Flow<Resource<Unit>>




}