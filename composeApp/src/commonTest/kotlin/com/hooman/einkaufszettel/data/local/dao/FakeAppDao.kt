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

class FakeAppDao : AppDao() {
    private val billTable = MutableStateFlow<List<BillEntity>>(emptyList())
    private val productTable = MutableStateFlow<List<ProductEntity>>(emptyList())
    private val itemTable = MutableStateFlow<List<ShoppingItemEntity>>(emptyList())


    override suspend fun upsertBillEntity(bill: BillEntity) {
        billTable.update { current ->
            current.filterNot { it.id == bill.id } + bill
        }
    }

    override fun getAllBills(userId: String): Flow<List<BillWithItemsAndProducts>> {
        return combine(billTable, itemTable, productTable){allBills, allItems, allProducts ->
            val bills = allBills.filter { it.userId == userId && userId.isNotBlank() }
            val items = allItems.filter { it.userId == userId && userId.isNotBlank() }
            val products = allProducts.filter { it.userId == userId && userId.isNotBlank() }
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

    override fun getBillById(id: String, userId: String): Flow<BillWithItemsAndProducts?> {
        return combine(billTable, itemTable, productTable){allBills, allItems, allProducts ->
            val bills = allBills.filter { it.userId == userId && userId.isNotBlank() }
            val items = allItems.filter { it.userId == userId && userId.isNotBlank() }
            val products = allProducts.filter { it.userId == userId && userId.isNotBlank() }
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

    override fun getBillByName(name: String, userId: String): Flow<List<BillWithItemsAndProducts>> {
        return combine(billTable, itemTable, productTable){allBills, allItems, allProducts ->
            val bills = allBills.filter { it.userId == userId && userId.isNotBlank() }
            val items = allItems.filter { it.userId == userId && userId.isNotBlank() }
            val products = allProducts.filter { it.userId == userId && userId.isNotBlank() }
            val targetBills = bills.filter { it.name.contains(name, ignoreCase = true) }

            targetBills.map { targetBill ->
                val targetItems = items.filter { it.billId == targetBill.id }

                val itemWithProducts = targetItems.map { shoppingItem ->
                    val relatedProduct = products.find { it.id == shoppingItem.productId }
                        ?: throw IllegalStateException("Product not found for this item")

                    ShoppingItemWithProduct(
                        item = shoppingItem,
                        product = relatedProduct
                    )
                }

                BillWithItemsAndProducts(
                    bill = targetBill,
                    items = itemWithProducts
                )
            }
        }
    }

    override suspend fun upsertShoppingItemEntity(item: ShoppingItemEntity) {
        itemTable.update { current ->
            current.filterNot { it.id == item.id } + item
        }
    }

    override suspend fun upsertProductEntity(product: ProductEntity) {
        productTable.update { current ->
            current.filterNot { it.id == product.id } + product
        }
    }

    override fun getProductById(id: String, userId: String): Flow<ProductEntity?> {
        return productTable.map { rows ->
            val products = rows.filter { it.userId == userId && userId.isNotBlank() }
            products.find { it.id == id }
        }
    }

    override fun getAllProducts(userId: String): Flow<List<ProductEntity>> {
        return productTable.map { rows -> rows.filter { it.userId == userId && userId.isNotBlank() } }
    }

    override fun getProductByName(name: String, userId: String): Flow<List<ProductEntity>> {
        return productTable.map { rows ->
            val products = rows.filter { it.userId == userId && userId.isNotBlank() }
            products.filter { it.name == name }
        }
    }

    override fun getShoppingItemsByBillId(billId: String, userId: String): Flow<List<ShoppingItemEntity>> {
        return itemTable.map { rows ->
            val items = rows.filter { it.userId == userId && userId.isNotBlank() }
            items.filter { it.billId == billId }
        }
    }

    override fun getProductsForShoppingItem(billId: String, userId: String): Flow<List<ShoppingDetails>> {
        return combine(itemTable, productTable){allItems, allProducts ->
            val items = allItems.filter { it.userId == userId && userId.isNotBlank() }
            val products = allProducts.filter { it.userId == userId && userId.isNotBlank() }
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
                        userId = item.userId,
                        syncStatus = item.syncStatus
                    )
                }else{
                    null
                }
            }
        }
    }

    override fun getAvailableProductsForShoppingItem(billId: String, userId: String): Flow<List<ProductEntity>> {
        return productTable.map { rows -> rows.filter { it.userId == userId && userId.isNotBlank() } }
    }

    override fun getCheckedProductsForShoppingItem(billId: String, userId: String): Flow<List<String>> {
        return itemTable.map { rows ->
            val items = rows.filter { it.userId == userId && userId.isNotBlank() }
            items
                .filter { it.billId == billId }
                .map { it.productId }
        }
    }

    override suspend fun deleteProduct(productId: String, userId: String) {
        productTable.update { products ->
            products.filterNot { it.id == productId && it.userId == userId }
        }
    }

    override suspend fun deleteShoppingItem(shoppingItemId: String, userId: String) {
        itemTable.update { items ->
            items.filterNot { it.id == shoppingItemId && it.userId == userId }
        }
    }

    override suspend fun deleteShoppingItemByProductAndBill(
        billId: String,
        productId: String, userId: String) {
        itemTable.update { items ->
            items.filterNot { it.billId == billId && it.productId == productId && it.userId == userId }
        }
    }

    override suspend fun updateShoppingItemCount(id: String, itemCount: Int, userId: String) {
        itemTable.update { items ->
            items.map { item ->
                if(item.id == id && item.userId == userId){
                    item.copy(itemCount = itemCount, syncStatus = SyncStatus.LSL)
                }else{
                    item
                }
            }
        }
    }

    override suspend fun deleteBill(billId: String, userId: String) {
        billTable.update { bills ->
            bills.filterNot { it.id == billId && it.userId == userId }
        }
    }

    override suspend fun updateShoppingItemCheckStatus(
        id: String,
        isChecked: Boolean, userId: String) {
        itemTable.update { items ->
            items.map { item ->
                if(item.id == id && item.userId == userId){
                    item.copy(isChecked = isChecked, syncStatus = SyncStatus.LSL)
                }else{
                    item
                }
            }
        }
    }

    override suspend fun updateShoppingItemDiscount(id: String, discount: Float, userId: String) {
        itemTable.update { items ->
            items.map { item ->
                if(item.id == id && item.userId == userId){
                    item.copy(discount = discount, syncStatus = SyncStatus.LSL)
                }else{
                    item
                }
            }
        }
    }

    override fun getAllBillsByDate(
        startDate: Long,
        endDate: Long, userId: String): Flow<List<BillWithItemsAndProducts>> {
        return combine(billTable, itemTable, productTable){allBills, allItems, allProducts ->
            val bills = allBills.filter { it.userId == userId && userId.isNotBlank() }
            val items = allItems.filter { it.userId == userId && userId.isNotBlank() }
            val products = allProducts.filter { it.userId == userId && userId.isNotBlank() }
            val filteredBills = bills.filter { bill ->
                val dateInMillis = bill.billDate.toEpochMilliseconds()
                dateInMillis >= startDate && dateInMillis < endDate
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

    override fun getProductIcons(userId: String): Flow<List<String>> {
        return productTable.map { rows ->
            val products = rows.filter { it.userId == userId && userId.isNotBlank() }
            products.map {
                it.image ?: ""
            }
        }
    }

    override fun getBillUnSyncData(syncStatus: SyncStatus, userId: String): Flow<List<BillEntity>> {
        return billTable.map { rows ->
            val bills = rows.filter { it.userId == userId && userId.isNotBlank() }
            bills.filter {
                it.syncStatus != syncStatus
            }
        }
    }

    override suspend fun updateBillSyncStatus(
        billId: String,
        syncStatus: SyncStatus, userId: String) {
        billTable.update { current ->
            current.map {
                if(it.id == billId && it.userId == userId) it.copy(syncStatus = syncStatus) else it
            }
        }
    }



    override fun getProductUnSyncData(syncStatus: SyncStatus, userId: String): Flow<List<ProductEntity>> {
        return productTable.map { rows ->
            val products = rows.filter { it.userId == userId && userId.isNotBlank() }
            products.filter {
                it.syncStatus != syncStatus
            }
        }
    }

    override suspend fun updateProductSyncStatus(
        productId: String,
        syncStatus: SyncStatus, userId: String) {
        productTable.update { products ->
            products.map {
                if(it.id == productId && it.userId == userId) it.copy(syncStatus = syncStatus) else it
            }
        }
    }



    override fun getShoppingItemUnSyncData(syncStatus: SyncStatus, userId: String): Flow<List<ShoppingDetails>> {
        return combine(itemTable, productTable){allItems, allProducts ->
            val items = allItems.filter { it.userId == userId && userId.isNotBlank() }
            val products = allProducts.filter { it.userId == userId && userId.isNotBlank() }
            val filteredItems = items.filter { it.syncStatus != syncStatus }
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
                        userId = filterItem.userId,
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
        syncStatus: SyncStatus, userId: String) {
        itemTable.update { items ->
            items.map { if(it.id == itemId && it.userId == userId) it.copy(syncStatus = syncStatus) else it }
        }
    }



    override suspend fun canWriteBill(id: String, userId: String) =
        billTable.value.none { it.id == id && it.userId != userId }

    override suspend fun canWriteProduct(id: String, userId: String) =
        productTable.value.none { it.id == id && it.userId != userId }

    override suspend fun canWriteItem(id: String, userId: String) =
        itemTable.value.none { it.id == id && it.userId != userId }

    override suspend fun ownsParents(billId: String, productId: String, userId: String) =
        billTable.value.any { it.id == billId && it.userId == userId } &&
            productTable.value.any { it.id == productId && it.userId == userId }

    override suspend fun readBill(id: String, userId: String) =
        billTable.value.find { it.id == id && it.userId == userId && userId.isNotBlank() }

    override suspend fun readProduct(id: String, userId: String) =
        productTable.value.find { it.id == id && it.userId == userId && userId.isNotBlank() }

    override suspend fun readShoppingItem(id: String, userId: String) =
        itemTable.value.find { it.id == id && it.userId == userId && userId.isNotBlank() }
}
