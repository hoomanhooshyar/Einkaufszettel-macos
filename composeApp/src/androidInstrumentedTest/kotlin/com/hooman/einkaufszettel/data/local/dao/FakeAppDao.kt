package com.hooman.einkaufszettel.data.local.dao

import com.hooman.einkaufszettel.data.local.entity.BillEntity
import com.hooman.einkaufszettel.data.local.entity.ProductEntity
import com.hooman.einkaufszettel.data.local.entity.ShoppingItemEntity
import com.hooman.einkaufszettel.data.local.entity.SyncStatus
import com.hooman.einkaufszettel.data.local.relation.BillWithItemsAndProducts
import com.hooman.einkaufszettel.data.local.relation.ShoppingItemWithProduct
import com.hooman.einkaufszettel.domain.model.ShoppingDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeAppDao : AppDao {
    private val billTable = MutableStateFlow<List<BillEntity>>(emptyList())
    private val productTable = MutableStateFlow<List<ProductEntity>>(emptyList())
    private val itemTable = MutableStateFlow<List<ShoppingItemEntity>>(emptyList())


    override suspend fun insertBill(bill: BillEntity) {
        billTable.update { current ->
            val list = current.toMutableList()
            list.removeIf { it.id == bill.id }
            list.add(bill)
            list
        }
    }

    override fun getAllBills(): Flow<List<BillWithItemsAndProducts>> {
        return combine(billTable, itemTable, productTable){bills, items, products ->
            bills.map { currentBill ->
                val targetItem = items.filter { it.billId == currentBill.id }
                val itemWithProduct = targetItem.map { shoppingItem ->
                    val relatedProduct = products.find { it.id == shoppingItem.productId } ?: throw IllegalStateException("No product found")

                    ShoppingItemWithProduct(
                        item = shoppingItem,
                        product = relatedProduct
                    )
                }
                BillWithItemsAndProducts(
                    bill = currentBill,
                    items = itemWithProduct
                )
            }
        }
    }

    override fun getBillById(id: String): Flow<BillWithItemsAndProducts?> {
        return combine(billTable, itemTable, productTable){bills, items, products ->
            val targetBill = bills.find { it.id == id } ?: return@combine null
            val targetItems = items.filter { it.billId == id }
            val itemsWithProducts = targetItems.map { shoppingItem ->
                val relatedProduct = products.find { it.id == shoppingItem.productId } ?: throw IllegalStateException("Product not found for this Item")

                ShoppingItemWithProduct(
                    item = shoppingItem,
                    product = relatedProduct
                )
            }

            BillWithItemsAndProducts(
                bill = targetBill,
                items = itemsWithProducts
            )
        }
    }

    override suspend fun insertShoppingItem(item: ShoppingItemEntity) {
        itemTable.update { current ->
            val list = current.toMutableList()
            list.removeIf { it.id == item.id }
            list.add(item)
            list
        }
    }

    override suspend fun insertProduct(product: ProductEntity) {
        productTable.update { current ->
            val list = current.toMutableList()
            list.removeIf { it.id == product.id }
            list.add(product)
            list
        }
    }

    override fun getProductById(id: String): Flow<ProductEntity?> {
        return productTable.map { products ->
            products.find { it.id == id }
        }
    }

    override fun getAllProducts(): Flow<List<ProductEntity>> {
        return productTable
    }

    override fun getProductByName(name: String): Flow<List<ProductEntity>> {
        return productTable.map { products ->
            products.filter { it.name == name }
        }
    }

    override fun getShoppingItemsByBillId(billId: String): Flow<List<ShoppingItemEntity>> {
        return itemTable.map { items ->
            items.filter { it.billId == billId }
        }
    }

    override fun getProductsForShoppingItem(billId: String): Flow<List<ShoppingDetails>> {
        return combine(itemTable, productTable){items, products ->
            val targetItems = items.filter { it.billId == billId }
            targetItems.mapNotNull { item ->
                val product = products.find { it.id == item.productId }
                if(product != null){
                    ShoppingDetails(
                        productId = product.id,
                        productImage = product.image ?: "",
                        productName = product.name,
                        productPrice = product.price,
                        shoppingItemId = item.id,
                        discount = item.discount,
                        isChecked = item.isChecked,
                        itemCount = item.itemCount,
                        billId = item.billId,
                        syncStatus = item.syncStatus
                    )
                }else{
                    null
                }
            }
        }
    }

    override fun getAvailableProductsForShoppingItem(billId: String): Flow<List<ProductEntity>> {
        return productTable
    }

    override fun getCheckedProductsForShoppingItem(billId: String): Flow<List<String>> {
        return itemTable.map { items ->
            items
                .filter { it.billId == billId }
                .map { it.productId }
        }
    }

    override suspend fun deleteProduct(product: ProductEntity) {
        productTable.update { products ->
            products.filterNot { it.id == product.id }
        }
    }

    override suspend fun deleteShoppingItem(shoppingItemId: String) {
        itemTable.update { items ->
            items.filterNot { it.id == shoppingItemId }
        }
    }

    override suspend fun deleteShoppingItemByProductAndBill(
        billId: String,
        productId: String
    ) {
        itemTable.update { items ->
            items.filterNot { it.billId == billId && it.productId == productId }
        }
    }

    override suspend fun updateShoppingItemCount(id: String, itemCount: Int) {
        itemTable.update { items ->
            items.map { item ->
                if(item.id == id){
                    item.copy(itemCount = itemCount)
                }else{
                    item
                }
            }
        }
    }

    override suspend fun deleteBill(bill: BillEntity) {
        billTable.update { bills ->
            bills.filterNot { it.id == bill.id }
        }
    }

    override suspend fun updateShoppingItemCheckStatus(
        id: String,
        isChecked: Boolean
    ) {
        itemTable.update { items ->
            items.map { item ->
                if(item.id == id){
                    item.copy(isChecked = isChecked)
                }else{
                    item
                }
            }
        }
    }

    override suspend fun updateShoppingItemDiscount(id: String, discount: Float) {
        itemTable.update { items ->
            items.map { item ->
                if(item.id == id){
                    item.copy(discount = discount)
                }else{
                    item
                }
            }
        }
    }

    override fun getAllBillsByDate(
        startDate: Long,
        endDate: Long
    ): Flow<List<BillWithItemsAndProducts>> {
        return combine(billTable, itemTable, productTable){bills, items, products ->
            val filteredBills = bills.filter { bill ->
                val dateInMillis = bill.billDate.toEpochMilliseconds()
                dateInMillis in startDate..endDate
            }
            filteredBills.map { currentBill ->
                val targetItems = items.filter { it.billId == currentBill.id }

                val itemsWithProducts = targetItems.map { shoppingItem ->
                    val relatedProduct = products.find { it.id == shoppingItem.productId } ?: throw IllegalStateException("Product not found")

                    ShoppingItemWithProduct(
                        item = shoppingItem,
                        product = relatedProduct
                    )
                }

                BillWithItemsAndProducts(
                    bill = currentBill,
                    items = itemsWithProducts
                )
            }
        }
    }

    override fun getProductIcons(): Flow<List<String>> {
        return productTable.map { products ->
            products.map {
                it.image ?: ""
            }
        }
    }

    override fun getBillUnSyncData(syncStatus: SyncStatus): Flow<List<BillEntity>> {
        return billTable.map { bills ->
            bills.filter {
                it.syncStatus == syncStatus
            }
        }
    }

    override suspend fun updateBillSyncStatus(
        billId: String,
        syncStatus: SyncStatus
    ) {
        billTable.update { current ->
            current.map {
                if(it.id == billId) it.copy(syncStatus = syncStatus) else it
            }
        }
    }

    override suspend fun insertBillList(bills: List<BillEntity>) {
        billTable.update { currentBills->
            val newIds = bills.map { it.id }.toSet()
            currentBills.filterNot { it.id in newIds } + bills
        }
    }

    override fun getProductUnSyncData(syncStatus: SyncStatus): Flow<List<ProductEntity>> {
        return productTable.map { products ->
            products.filter {
                it.syncStatus == syncStatus
            }
        }
    }

    override suspend fun updateProductSyncStatus(
        productId: String,
        syncStatus: SyncStatus
    ) {
        productTable.update { products ->
            products.map {
                if(it.id == productId) it.copy(syncStatus = syncStatus) else it
            }
        }
    }

    override suspend fun insertProductList(products: List<ProductEntity>) {
        productTable.update { currentProducts ->
            val newIds = products.map { it.id }.toSet()
            currentProducts.filterNot { it.id in newIds } + products
        }
    }

    override fun getShoppingItemUnSyncData(syncStatus: SyncStatus): Flow<List<ShoppingDetails>> {
        return combine(itemTable, productTable){items, products ->
            val filteredItems = items.filter { it.syncStatus == syncStatus }
            filteredItems.mapNotNull { filterItem ->
                val product = products.find { it.id == filterItem.productId }
                if(product != null){
                    ShoppingDetails(
                        productId = product.id,
                        productImage = product.image ?: "",
                        productName = product.name,
                        productPrice = product.price,
                        shoppingItemId = filterItem.id,
                        discount = filterItem.discount,
                        isChecked = filterItem.isChecked,
                        itemCount = filterItem.itemCount,
                        billId = filterItem.billId,
                        syncStatus = filterItem.syncStatus
                    )
                }else{
                    null
                }
            }

        }
    }

    override suspend fun updateShoppingItemSyncStatus(
        itemId: String,
        syncStatus: SyncStatus
    ) {
        itemTable.update { items ->
            items.map { if(it.id == itemId) it.copy(syncStatus = syncStatus) else it }
        }
    }

    override suspend fun insertItemList(items: List<ShoppingItemEntity>) {
        itemTable.update { currentItems ->
            val newIds = items.map { it.id }.toSet()
            currentItems.filterNot { it.id in newIds } + items
        }
    }
}