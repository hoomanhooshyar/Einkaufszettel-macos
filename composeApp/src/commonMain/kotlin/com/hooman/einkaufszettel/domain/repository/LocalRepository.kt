package com.hooman.einkaufszettel.domain.repository

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.data.local.entity.ProductEntity
import com.hooman.einkaufszettel.domain.model.Bill
import com.hooman.einkaufszettel.domain.model.Product
import com.hooman.einkaufszettel.domain.model.ShoppingDetails
import com.hooman.einkaufszettel.domain.model.ShoppingItem
import kotlinx.coroutines.flow.Flow

interface LocalRepository {
    fun getAllBills(): Flow<Resource<List<Bill>>>
    fun getBillById(billId: String): Flow<Resource<Bill>>
    suspend fun insertBill(bill: Bill): Resource<Unit>
    suspend fun deleteBill(bill: Bill): Resource<Unit>
    suspend fun insertShoppingItem(shoppingItem: ShoppingItem,billId: String): Resource<Unit>
    suspend fun deleteShoppingItem(shoppingItemId: String): Resource<Unit>
    fun getAllProducts(): Flow<Resource<List<Product>>>

    fun getProductByName(name: String): Flow<Resource<List<Product>>>
    fun getProductById(productId: String): Flow<Resource<Product>>
    fun getProductIcons():Flow<Resource<List<String>>>
    suspend fun insertProduct(product: Product): Resource<Unit>
    suspend fun deleteProduct(product: Product): Resource<Unit>

    fun getAllShoppingItemsByBillId(billId: String): Flow<Resource<List<ShoppingItem>>>

    fun getAvailableProductsForShoppingItem(billId: String): Flow<Resource<List<Product>>>

    fun getCheckedProductsForShoppingItem(billId: String): Flow<Resource<List<String>>>

    fun getProductsForShoppingItem(billId: String): Flow<Resource<List<ShoppingDetails>>>

    suspend fun updateShoppingItemCheckStatus(shoppingItemId: String, isChecked: Boolean): Resource<Unit>
    suspend fun updateSHoppingItemCount(shoppingItemId: String, itemCount: Int): Resource<Unit>
}