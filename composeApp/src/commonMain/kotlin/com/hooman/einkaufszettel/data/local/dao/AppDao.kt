@file:OptIn(ExperimentalTime::class)

package com.hooman.einkaufszettel.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import androidx.room.Transaction
import androidx.room.TypeConverters
import com.hooman.einkaufszettel.data.local.converter.Converter
import com.hooman.einkaufszettel.data.local.entity.BillEntity
import com.hooman.einkaufszettel.data.local.entity.ProductEntity
import com.hooman.einkaufszettel.data.local.entity.ShoppingItemEntity
import com.hooman.einkaufszettel.data.local.entity.SyncStatus
import com.hooman.einkaufszettel.data.local.relation.BillWithItemsAndProducts
import com.hooman.einkaufszettel.domain.model.ShoppingDetails
import kotlinx.coroutines.flow.Flow
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Dao
abstract class AppDao {

    @Transaction
    open suspend fun insertBill(bill: BillEntity) {
        require(bill.userId.isNotBlank()) { "Missing account owner" }
        require(canWriteBill(bill.id, bill.userId)) { "Cannot transfer ownership of an existing record" }
        if (bill.syncStatus == SyncStatus.SUCCESS) {
            val current = readBill(bill.id, bill.userId)
            if (current != null && current.syncStatus != SyncStatus.SUCCESS) return
        }
        upsertBillEntity(bill)
    }

    @Upsert
    protected abstract suspend fun upsertBillEntity(bill: BillEntity)

    @Query("SELECT NOT EXISTS(SELECT 1 FROM bill WHERE id = :id AND userId != :userId)")
    protected abstract suspend fun canWriteBill(id: String, userId: String): Boolean

    @Transaction
    @Query("SELECT * FROM bill WHERE userId = :userId AND userId != '' ORDER BY billDate DESC")
    abstract fun getAllBills(userId: String): Flow<List<BillWithItemsAndProducts>>

    @Transaction
    @Query("SELECT * FROM bill WHERE userId = :userId AND userId != '' AND id = :id LIMIT 1")
    abstract fun getBillById(id: String, userId: String): Flow<BillWithItemsAndProducts?>

    @Transaction
    @Query("SELECT * FROM bill WHERE userId = :userId AND userId != '' AND name LIKE '%' || :name || '%'")
    abstract fun getBillByName(name: String, userId: String): Flow<List<BillWithItemsAndProducts>>

    @Transaction
    open suspend fun insertShoppingItem(item: ShoppingItemEntity) {
        require(item.userId.isNotBlank()) { "Missing account owner" }
        require(canWriteItem(item.id, item.userId)) { "Cannot transfer ownership of an existing record" }
        require(ownsParents(item.billId, item.productId, item.userId)) { "Shopping item parents belong to a different account" }
        if (item.syncStatus == SyncStatus.SUCCESS) {
            val current = readShoppingItem(item.id, item.userId)
            if (current != null && current.syncStatus != SyncStatus.SUCCESS) return
        }
        upsertShoppingItemEntity(item)
    }

    @Upsert
    protected abstract suspend fun upsertShoppingItemEntity(item: ShoppingItemEntity)

    @Query("SELECT NOT EXISTS(SELECT 1 FROM shopping_items WHERE id = :id AND userId != :userId)")
    protected abstract suspend fun canWriteItem(id: String, userId: String): Boolean



    @Transaction
    open suspend fun insertProduct(product: ProductEntity) {
        require(product.userId.isNotBlank()) { "Missing account owner" }
        require(canWriteProduct(product.id, product.userId)) { "Cannot transfer ownership of an existing record" }
        if (product.syncStatus == SyncStatus.SUCCESS) {
            val current = readProduct(product.id, product.userId)
            if (current != null && current.syncStatus != SyncStatus.SUCCESS) return
        }
        upsertProductEntity(product)
    }

    @Upsert
    protected abstract suspend fun upsertProductEntity(product: ProductEntity)

    @Query("SELECT NOT EXISTS(SELECT 1 FROM product WHERE id = :id AND userId != :userId)")
    protected abstract suspend fun canWriteProduct(id: String, userId: String): Boolean

    @Transaction
    @Query("SELECT * FROM product WHERE userId = :userId AND userId != '' AND id = :id")
    abstract fun getProductById(id: String, userId: String): Flow<ProductEntity?>

    @Transaction
    @Query("SELECT * FROM product WHERE userId = :userId AND userId != ''")
    abstract fun getAllProducts(userId: String): Flow<List<ProductEntity>>

    @Transaction
    @Query("SELECT * FROM product WHERE userId = :userId AND userId != '' AND name LIKE '%' || :name || '%'")
    abstract fun getProductByName(name: String, userId: String): Flow<List<ProductEntity>>

    @Transaction
    @Query("SELECT * FROM shopping_items WHERE userId = :userId AND userId != '' AND billId = :billId")
    abstract fun getShoppingItemsByBillId(billId: String, userId: String): Flow<List<ShoppingItemEntity>>


    @Query("""
        SELECT p.id as productId, p.image as productImage,
                p.name as productName, p.price as productPrice,
                si.userId as userId, si.billId as billId, si.syncStatus as syncStatus,
                si.id as shoppingItemId, si.isChecked as isChecked,
                si.itemCount as itemCount,
                si.discount as discount
                FROM product p
                INNER JOIN shopping_items si 
                ON p.id = si.productId AND si.billId = :billId
                WHERE p.userId = :userId AND si.userId = :userId AND :userId != ''
    """)
    abstract fun getProductsForShoppingItem(billId: String, userId: String): Flow<List<ShoppingDetails>>


    @Transaction
    @Query("""
        SELECT * FROM product
        WHERE userId = :userId AND userId != '' AND id NOT IN(
        SELECT productId FROM shopping_items WHERE userId = :userId AND userId != '' AND billId = :billId)
    """)
    abstract fun getAvailableProductsForShoppingItem(billId: String, userId: String): Flow<List<ProductEntity>>

    @Transaction
    @Query("""
        SELECT productId FROM shopping_items
        WHERE userId = :userId AND userId != '' AND billId = :billId
    """)
    abstract fun getCheckedProductsForShoppingItem(billId: String, userId: String): Flow<List<String>>

    @Query("DELETE FROM product WHERE id = :productId AND userId = :userId AND userId != ''")
    abstract suspend fun deleteProduct(productId: String, userId: String)

    @Query("DELETE FROM shopping_items WHERE userId = :userId AND userId != '' AND id = :shoppingItemId")
    abstract suspend fun deleteShoppingItem(shoppingItemId: String, userId: String)

    @Query("DELETE FROM shopping_items WHERE userId = :userId AND userId != '' AND billId = :billId AND productId = :productId")
    abstract suspend fun deleteShoppingItemByProductAndBill(billId: String, productId: String, userId: String)

    @Query("UPDATE shopping_items SET itemCount = :itemCount, syncStatus = 'LSL' WHERE userId = :userId AND userId != '' AND id = :id")
    abstract suspend fun updateShoppingItemCount(id: String, itemCount: Int, userId: String)

    @Query("DELETE FROM bill WHERE id = :billId AND userId = :userId AND userId != ''")
    abstract suspend fun deleteBill(billId: String, userId: String)

    @Query("UPDATE shopping_items SET isChecked = :isChecked, syncStatus = 'LSL' WHERE userId = :userId AND userId != '' AND id = :id")
    abstract suspend fun updateShoppingItemCheckStatus(id: String, isChecked: Boolean, userId: String)

    @Query("UPDATE shopping_items SET discount = :discount, syncStatus = 'LSL' WHERE userId = :userId AND userId != '' AND id = :id")
    abstract suspend fun updateShoppingItemDiscount(id: String, discount: Float, userId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM bill WHERE id = :billId AND userId = :userId AND userId != '') AND EXISTS(SELECT 1 FROM product WHERE id = :productId AND userId = :userId AND userId != '')")
    protected abstract suspend fun ownsParents(billId: String, productId: String, userId: String): Boolean

    //Report Queries

    @Transaction
    @Query("SELECT * FROM bill WHERE userId = :userId AND userId != '' AND billDate >= :startDate AND billDate < :endDate ORDER BY billDate DESC")
    abstract fun getAllBillsByDate(startDate: Long, endDate: Long, userId: String): Flow<List<BillWithItemsAndProducts>>

    @Transaction
    @Query("SELECT image FROM product WHERE userId = :userId AND userId != '' AND image IS NOT NULL")
    abstract fun getProductIcons(userId: String): Flow<List<String>>

    /*
        Sync Queries
    * */

    //**************Bill Entity****************
    @Query(" SELECT * FROM bill WHERE userId = :userId AND userId != '' AND syncStatus != :syncStatus")
    abstract fun getBillUnSyncData(syncStatus: SyncStatus = SyncStatus.SUCCESS, userId: String): Flow<List<BillEntity>>

    //Update SyncStatus
    @Query("UPDATE bill SET syncStatus = :syncStatus WHERE userId = :userId AND userId != '' AND id = :billId")
    abstract suspend fun updateBillSyncStatus(billId: String, syncStatus: SyncStatus, userId: String)

    //Insert a List
    @Transaction
    open suspend fun insertBillList(bills: List<BillEntity>) {
        bills.forEach { insertBill(it) }
    }

    //***********************Product Entity*******************
    @Query(" SELECT * FROM product WHERE userId = :userId AND userId != '' AND syncStatus != :syncStatus")
    abstract fun getProductUnSyncData(syncStatus: SyncStatus = SyncStatus.SUCCESS, userId: String): Flow<List<ProductEntity>>

    //Update SyncStatus
    @Query("UPDATE product SET syncStatus = :syncStatus WHERE userId = :userId AND userId != '' AND id = :productId")
    abstract suspend fun updateProductSyncStatus(productId: String, syncStatus: SyncStatus, userId: String)

    //Insert a List
    @Transaction
    open suspend fun insertProductList(products: List<ProductEntity>) {
        products.forEach { insertProduct(it) }
    }

    //***********************ShoppingItem Entity*******************
    @Query("""
    SELECT 
        s.userId AS userId, s.id AS shoppingItemId,
        s.billId AS billId,
        s.discount AS discount,
        s.isChecked AS isChecked,
        s.itemCount AS itemCount,
        s.syncStatus AS syncStatus,
        
        p.id AS productId,
        p.image AS productImage,
        p.name AS productName,
        p.price AS productPrice
        
    FROM shopping_items s
    INNER JOIN product p ON s.productId = p.id
    WHERE s.userId = :userId AND p.userId = :userId AND :userId != '' AND s.syncStatus != :syncStatus
""")
    abstract fun getShoppingItemUnSyncData(syncStatus: SyncStatus = SyncStatus.SUCCESS, userId: String): Flow<List<ShoppingDetails>>

    //Update SyncStatus
    @Query("UPDATE shopping_items SET syncStatus = :syncStatus WHERE userId = :userId AND userId != '' AND id = :itemId")
    abstract suspend fun updateShoppingItemSyncStatus(itemId: String, syncStatus: SyncStatus, userId: String)

    //Insert List
    @Transaction
    open suspend fun insertItemList(items: List<ShoppingItemEntity>) {
        items.forEach { insertShoppingItem(it) }
    }


    @Query("SELECT * FROM bill WHERE id = :id AND userId = :userId AND userId != ''")
    abstract suspend fun readBill(id: String, userId: String): BillEntity?

    // Compare and acknowledge in one Room transaction: never clean a newer edit.
    @Transaction
    open suspend fun acknowledgeBill(uploaded: BillEntity, status: SyncStatus): Boolean {
        if (uploaded.userId.isBlank() || readBill(uploaded.id, uploaded.userId) != uploaded) return false
        upsertBillEntity(uploaded.copy(syncStatus = status))
        return true
    }


    @Query("SELECT * FROM product WHERE id = :id AND userId = :userId AND userId != ''")
    abstract suspend fun readProduct(id: String, userId: String): ProductEntity?

    // Compare and acknowledge in one Room transaction: never clean a newer edit.
    @Transaction
    open suspend fun acknowledgeProduct(uploaded: ProductEntity, status: SyncStatus): Boolean {
        if (uploaded.userId.isBlank() || readProduct(uploaded.id, uploaded.userId) != uploaded) return false
        upsertProductEntity(uploaded.copy(syncStatus = status))
        return true
    }


    @Query("SELECT * FROM shopping_items WHERE id = :id AND userId = :userId AND userId != ''")
    abstract suspend fun readShoppingItem(id: String, userId: String): ShoppingItemEntity?

    // Compare and acknowledge in one Room transaction: never clean a newer edit.
    @Transaction
    open suspend fun acknowledgeShoppingItem(uploaded: ShoppingItemEntity, status: SyncStatus): Boolean {
        if (uploaded.userId.isBlank() || readShoppingItem(uploaded.id, uploaded.userId) != uploaded) return false
        upsertShoppingItemEntity(uploaded.copy(syncStatus = status))
        return true
    }

}
