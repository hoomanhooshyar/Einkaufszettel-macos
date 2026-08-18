package com.hooman.einkaufszettel.data.repositrory

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.data.local.entity.BillEntity
import com.hooman.einkaufszettel.data.local.entity.SyncStatus
import com.hooman.einkaufszettel.domain.model.Bill
import com.hooman.einkaufszettel.domain.model.Product
import com.hooman.einkaufszettel.domain.model.ShoppingDetails
import com.hooman.einkaufszettel.domain.model.ShoppingItem
import com.hooman.einkaufszettel.domain.repository.LocalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeLocalRepository: LocalRepository {

    var shouldErrorThrow = false
    val error = "Error"
    val bills = MutableStateFlow<List<Bill>>(emptyList())
    val shoppingItems = MutableStateFlow<List<ShoppingItem>>(emptyList())
    val products = MutableStateFlow<List<Product>>(emptyList())
    val productIcons = MutableStateFlow<List<String>>(emptyList())


    override fun getAllBills(): Flow<Resource<List<Bill>>> {
        return bills.map {
            Resource.Success(it)
        }
    }

    override fun getBillById(billId: String): Flow<Resource<Bill>> {
        return bills.map { currentBills ->
            if(shouldErrorThrow){
                Resource.Error(error)
            }else{
                val bill = currentBills.filter { it.id == billId }
                Resource.Success(bill.first())
            }

        }
    }

    override fun getBillByName(name: String): Flow<Resource<List<Bill>>> {
        return bills.map { currentBills ->
            if(shouldErrorThrow){
                Resource.Error(error)
            }else{
                val bill = currentBills.filter { it.name.contains(name) }
                Resource.Success(data = bill)
            }
        }
    }

    override fun getBillByDate(
        startDate: Long,
        endDate: Long
    ): Flow<Resource<List<Bill>>> {
        return bills.map { currentBills ->
            if(shouldErrorThrow){
                Resource.Error(error)
            }else{
                val bill = currentBills.filter { it.billDate.toEpochMilliseconds() > startDate && it.billDate.toEpochMilliseconds() < endDate }
                Resource.Success(bill)
            }
        }
    }

    override suspend fun insertBill(bill: Bill): Resource<Unit> {
        bills.update { currentBills ->
            currentBills.filterNot { it.id == bill.id } + bill
        }
        return Resource.Success(Unit)
    }

    override suspend fun deleteBill(bill: Bill): Resource<Unit> {
        bills.update { currentBills ->
            currentBills.filterNot { it.id == bill.id }
        }
        return Resource.Success(Unit)
    }

    override suspend fun insertShoppingItem(
        shoppingItem: ShoppingItem,
        billId: String
    ): Resource<Unit> {
        shoppingItems.update { currentItems ->
            currentItems.filterNot { it.id == shoppingItem.id && it.billId == billId } + shoppingItem
        }
        return Resource.Success(Unit)
    }

    override suspend fun deleteShoppingItem(shoppingItemId: String): Resource<Unit> {
        shoppingItems.update { currentItems ->
            currentItems.filterNot { it.id == shoppingItemId }
        }
        return Resource.Success(Unit)
    }

    override suspend fun deleteShoppingItemByProductAndBill(
        billId: String,
        productId: String
    ): Resource<Unit> {
        shoppingItems.update { currentItems ->
            currentItems.filterNot { it.billId == billId && it.productId == productId }
        }
        return Resource.Success(Unit)
    }

    override fun getAllProducts(): Flow<Resource<List<Product>>> {
        return products.map {
            Resource.Success(it)
        }
    }

    override fun getProductByName(name: String): Flow<Resource<List<Product>>> {
        return products.map { currentProducts ->
            if(shouldErrorThrow){
                Resource.Error(error)
            }else{
                val product = currentProducts.filter { it.name == name }
                Resource.Success(product)
            }
        }
    }

    override fun getProductById(productId: String): Flow<Resource<Product>> {
        return products.map { currentProducts ->
            if(shouldErrorThrow){
                Resource.Error(error)
            }else{
                val product = currentProducts.filter { it.id == productId }
                Resource.Success(product.first())
            }
        }
    }

    override fun getProductIcons(): Flow<Resource<List<String>>> {
        return productIcons.map {
            Resource.Success(it)
        }
    }

    override suspend fun insertProduct(product: Product): Resource<Unit> {
        products.update { currentProduct ->
            currentProduct.filterNot { it.id == product.id } + product
        }
        return Resource.Success(Unit)
    }

    override suspend fun deleteProduct(product: Product): Resource<Unit> {
        products.update { currentProduct ->
            currentProduct.filterNot { it.id == product.id }
        }
        return Resource.Success(Unit)
    }

    override fun getAllShoppingItemsByBillId(billId: String): Flow<Resource<List<ShoppingItem>>> {
         return shoppingItems.map { currentItems ->
             if(shouldErrorThrow){
                 Resource.Error(error)
             }else{
                 val result = currentItems.filter { it.billId == billId }
                 Resource.Success(result)
             }
        }
    }

    override fun getAvailableProductsForShoppingItem(billId: String): Flow<Resource<List<Product>>> {
        return combine(products, shoppingItems){currentProduct, currentItems ->
            val usedProductIds = currentItems
                .filter { it.billId == billId }
                .map { it.productId }
                .toSet()

            val result = currentProduct.filterNot { product ->
                product.id in usedProductIds
            }

            Resource.Success(result)
        }
    }

    override fun getCheckedProductsForShoppingItem(billId: String): Flow<Resource<List<String>>> {
        return shoppingItems.map { currentItems ->
            if(shouldErrorThrow){
                Resource.Error(error)
            }else{
                val result = currentItems
                    .filter { it.billId == billId && it.isChecked }
                    .map { it.productName }
                Resource.Success(result)
            }

        }
    }

    override fun getProductsForShoppingItem(billId: String): Flow<Resource<List<ShoppingDetails>>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateShoppingItemCheckStatus(
        shoppingItemId: String,
        isChecked: Boolean
    ): Resource<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun updateSHoppingItemCount(
        shoppingItemId: String,
        itemCount: Int
    ): Resource<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun updateShoppingItemDiscount(
        shoppingItemId: String,
        discount: Float
    ): Resource<Unit> {
        TODO("Not yet implemented")
    }

    override fun getBillUnSyncData(syncStatus: SyncStatus): Flow<Resource<List<Bill>>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateBillSyncStatus(
        billId: String,
        syncStatus: SyncStatus
    ): Resource<Unit> {
        val currentBills = bills.value
        val exists = currentBills.any { it.id == billId }

        return if(exists){
            bills.update { list ->
                list.map { bill ->
                    if(bill.id == billId) bill.copy(syncStatus = syncStatus) else bill
                }
            }
            Resource.Success(Unit)
        }else{
            Resource.Error("Bill not found")
        }
    }

    override fun getProductUnSyncData(syncStatus: SyncStatus): Flow<Resource<List<Product>>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateProductSyncStatus(
        productId: String,
        syncStatus: SyncStatus
    ): Resource<Unit> {
        TODO("Not yet implemented")
    }

    override fun getShoppingItemUnSyncData(syncStatus: SyncStatus): Flow<Resource<List<ShoppingDetails>>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateShoppingItemSyncStatus(
        itemId: String,
        syncStatus: SyncStatus
    ): Resource<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun insertItemList(items: List<ShoppingItem>): Resource<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun insertBillList(bills: List<Bill>): Resource<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun insertProductList(products: List<Product>): Resource<Unit> {
        TODO("Not yet implemented")
    }
}