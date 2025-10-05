package com.hooman.einkaufszettel.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.hooman.einkaufszettel.data.local.entity.BillEntity
import com.hooman.einkaufszettel.data.local.entity.ProductEntity
import com.hooman.einkaufszettel.data.local.entity.ShoppingItemEntity
import com.hooman.einkaufszettel.data.local.relation.BillWithItemsAndProducts
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBill(bill: BillEntity)

    @Transaction
    @Query("SELECT * FROM bill ORDER BY billDate DESC")
    fun getAllBills(): Flow<List<BillWithItemsAndProducts?>>

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
    fun getAllProducts(): Flow<List<ProductEntity?>>

    @Transaction
    @Query("SELECT * FROM product WHERE name LIKE '%' || :name || '%'")
    fun getProductByName(name: String): Flow<List<ProductEntity?>>

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    @Delete
    suspend fun deleteShoppingItem(item: ShoppingItemEntity)

    @Delete
    suspend fun deleteBill(bill: BillEntity)

}