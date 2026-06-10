package com.hooman.einkaufszettel.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.data.local.entity.BillEntity
import com.hooman.einkaufszettel.data.local.entity.ProductEntity
import com.hooman.einkaufszettel.data.local.entity.ShoppingItemEntity
import com.hooman.einkaufszettel.data.local.relation.BillWithItemsAndProducts
import com.hooman.einkaufszettel.domain.model.ShoppingDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBill(bill: BillEntity)

    @Transaction
    @Query("SELECT * FROM bill ORDER BY billDate DESC")
    fun getAllBills(): Flow<List<BillWithItemsAndProducts>>

    @Transaction
    @Query("SELECT * FROM bill WHERE id = :id LIMIT 1")
    fun getBillById(id: String): Flow<BillWithItemsAndProducts?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShoppingItem(item: ShoppingItemEntity)



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Transaction
    @Query("SELECT * FROM product WHERE id = :id")
    fun getProductById(id: String): Flow<ProductEntity?>

    @Transaction
    @Query("SELECT * FROM product")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Transaction
    @Query("SELECT * FROM product WHERE name LIKE '%' || :name || '%'")
    fun getProductByName(name: String): Flow<List<ProductEntity>>

    @Transaction
    @Query("SELECT * FROM shopping_items WHERE billId = :billId")
    fun getShoppingItemsByBillId(billId: String): Flow<List<ShoppingItemEntity>>


    @Query("""
        SELECT p.id as productId, p.image as productImage,
                p.name as productName, p.price as productPrice,
                si.id as shoppingItemId, si.isChecked as isChecked,
                si.itemCount as itemCount 
                FROM product p
                INNER JOIN shopping_items si 
                ON p.id = si.productId AND si.billId = :billId
    """)
    fun getProductsForShoppingItem(billId: String): Flow<List<ShoppingDetails>>


    @Transaction
    @Query("""
        SELECT * FROM product
        WHERE id NOT IN(
        SELECT productId FROM shopping_items WHERE billId = :billId)
    """)
    fun getAvailableProductsForShoppingItem(billId: String): Flow<List<ProductEntity>>

    @Transaction
    @Query("""
        SELECT productId FROM shopping_items
        WHERE billId = :billId
    """)
    fun getCheckedProductsForShoppingItem(billId: String): Flow<List<String>>

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    @Query("DELETE FROM shopping_items WHERE id = :shoppingItemId")
    suspend fun deleteShoppingItem(shoppingItemId: String)

    @Query("UPDATE shopping_items SET itemCount = :itemCount WHERE id = :id")
    suspend fun updateSHoppingItemCount(id: String, itemCount: Int)

    @Delete
    suspend fun deleteBill(bill: BillEntity)

    @Query("UPDATE shopping_items SET isChecked = :isChecked WHERE id = :id")
    suspend fun updateShoppingItemCheckStatus(id: String, isChecked: Boolean)

}