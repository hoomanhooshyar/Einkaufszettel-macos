package com.hooman.einkaufszettel.data.repository

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.data.local.dao.AppDao
import com.hooman.einkaufszettel.data.local.entity.ShoppingItemEntity
import com.hooman.einkaufszettel.data.local.entity.SyncStatus
import com.hooman.einkaufszettel.data.mapper.toDomain
import com.hooman.einkaufszettel.data.mapper.toEntity
import com.hooman.einkaufszettel.data.mapper.toProduct
import com.hooman.einkaufszettel.data.mapper.toProductEntity
import com.hooman.einkaufszettel.data.mapper.toShoppingItem
import com.hooman.einkaufszettel.domain.model.Bill
import com.hooman.einkaufszettel.domain.model.Product
import com.hooman.einkaufszettel.domain.model.ShoppingDetails
import com.hooman.einkaufszettel.domain.model.ShoppingItem
import com.hooman.einkaufszettel.domain.repository.LocalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlin.time.ExperimentalTime

class LocalRepositoryImpl(
    private val dao: AppDao
): LocalRepository {
    override fun getAllBills(): Flow<Resource<List<Bill>>> {
        return dao.getAllBills()
            .map { bills ->
                Resource.Success(data = bills.map { it.toDomain() }) as Resource<List<Bill>>
            }
            .onStart {
                emit(Resource.Loading())
            }
            .catch { e ->
                emit(Resource.Error(e.message))
            }
    }

    override fun getBillById(billId: String): Flow<Resource<Bill>> {
        return dao.getBillById(billId)
            .map { bill ->
                Resource.Success(data = bill?.toDomain()) as Resource<Bill>
            }
            .onStart {
                emit(Resource.Loading())
            }
            .catch { e ->
                emit(Resource.Error(e.message))
            }
    }

    override fun getBillByName(name: String): Flow<Resource<List<Bill>>> {
        return dao.getBillByName(name)
            .map { bills ->
                Resource.Success(data = bills.map { it.toDomain() }) as Resource<List<Bill>>
            }
            .onStart {
                emit(Resource.Loading())
            }
            .catch {e ->
                emit(Resource.Error(e.message))
            }
    }

    @OptIn(ExperimentalTime::class)
    override fun getBillByDate(
        startDate: Long,
        endDate: Long
    ): Flow<Resource<List<Bill>>>{
        return dao.getAllBillsByDate(
            startDate = startDate,
            endDate = endDate
        )
            .map { bills ->
                Resource.Success(data = bills.map { it.toDomain() }) as Resource<List<Bill>>
            }
            .onStart {
                emit(Resource.Loading())
            }
            .catch { e ->
                emit(Resource.Error(e.message))
            }
    }

    override suspend fun insertBill(bill: Bill): Resource<Unit>  {
        return try {
            dao.insertBill(bill.toEntity())
            Resource.Success(Unit)
        }catch (e: Exception){
            Resource.Error(e.message)
        }
    }

    override suspend fun deleteBill(bill: Bill): Resource<Unit> {
        return try {
            dao.deleteBill(bill.toEntity())
            Resource.Success(Unit)
        }catch (e: Exception){
            Resource.Error(e.message)
        }
    }

    override suspend fun insertShoppingItem(
        shoppingItem: ShoppingItem,
        billId: String
    ):Resource<Unit> {
        return try {
            dao.insertShoppingItem(shoppingItem.toEntity())
            Resource.Success(Unit)
        }catch (e: Exception){
            Resource.Error(e.message)
        }
    }

    override suspend fun deleteShoppingItem(
        shoppingItemId: String
    ):Resource<Unit>{
        return try {
            dao.deleteShoppingItem(shoppingItemId = shoppingItemId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun deleteShoppingItemByProductAndBill(
        billId: String,
        productId: String
    ): Resource<Unit> {
        return try {
            dao.deleteShoppingItemByProductAndBill(billId, productId)
            Resource.Success(Unit)
        }catch (e: Exception){
            Resource.Error(e.message)
        }
    }

    override fun getAllProducts(): Flow<Resource<List<Product>>> {
        return dao.getAllProducts()
            .map { products ->
                Resource.Success(data = products.map { it.toProduct() }) as Resource<List<Product>>
            }
            .onStart {
                emit(Resource.Loading())
            }
            .catch { e ->
                emit(Resource.Error(e.message))
            }
    }

    override fun getProductByName(name: String): Flow<Resource<List<Product>>>{
        return dao.getProductByName(name)
            .map { products ->
                Resource.Success(data =  products.map { it.toProduct() }) as Resource<List<Product>>
            }
            .onStart {
                emit(Resource.Loading())
            }
            .catch { e ->
                emit(Resource.Error(e.message))
            }
    }

    override fun getProductById(productId: String): Flow<Resource<Product>> {
        return dao.getProductById(productId)
            .map { product ->
                Resource.Success(data = product?.toProduct()) as Resource<Product>
            }
            .onStart {
                emit(Resource.Loading())
            }
            .catch { e ->
                emit(Resource.Error(e.message))
            }
    }

    override fun getProductIcons(): Flow<Resource<List<String>>> {
        return dao.getProductIcons()
            .map { icons ->
                Resource.Success(data = icons) as Resource<List<String>>
            }
            .onStart {
                emit(Resource.Loading())
            }
            .catch { e ->
                emit(Resource.Error(e.message))
            }
    }

    override fun getAllShoppingItemsByBillId(billId: String): Flow<Resource<List<ShoppingItem>>>{
        return dao.getShoppingItemsByBillId(billId)
            .map { items ->
                Resource.Success(data = items.map { it.toShoppingItem() }) as Resource<List<ShoppingItem>>
            }
            .onStart {
                emit(Resource.Loading())
            }
            .catch { e ->
                emit(Resource.Error(e.message))
            }
    }

    override fun getAvailableProductsForShoppingItem(billId: String): Flow<Resource<List<Product>>>{
        return dao.getAvailableProductsForShoppingItem(billId)
            .map { products ->
                Resource.Success(data = products.map { it.toProduct() }) as Resource<List<Product>>
            }
            .onStart {
                emit(Resource.Loading())
            }
            .catch { e ->
                emit(Resource.Error(e.message))
            }
    }

    override fun getCheckedProductsForShoppingItem(billId: String): Flow<Resource<List<String>>>{
        return dao.getCheckedProductsForShoppingItem(billId)
            .map { checkedProducts ->
                Resource.Success(data = checkedProducts) as Resource<List<String>>
            }
            .onStart {
                emit(Resource.Loading())
            }
            .catch { e ->
                emit(Resource.Error(e.message))
            }

    }

    override fun getProductsForShoppingItem(billId: String): Flow<Resource<List<ShoppingDetails>>> {
        return dao.getProductsForShoppingItem(billId)
            .map { shoppingDetails ->
                Resource.Success(data = shoppingDetails) as Resource<List<ShoppingDetails>>
            }
            .onStart {
                emit(Resource.Loading())
            }
            .catch { e ->
                emit(Resource.Error(e.message))
            }
    }

    override suspend fun updateShoppingItemCheckStatus(
        shoppingItemId: String,
        isChecked: Boolean
    ):Resource<Unit>{
        return try {
            dao.updateShoppingItemCheckStatus(shoppingItemId, isChecked)
            Resource.Success(Unit)
        }catch (e: Exception){
            Resource.Error(e.message)
        }
    }

    override suspend fun updateSHoppingItemCount(
        shoppingItemId: String,
        itemCount: Int
    ):Resource<Unit> {
        return try {
            dao.updateShoppingItemCount(shoppingItemId, itemCount)
            Resource.Success(Unit)
        }catch (e: Exception){
            Resource.Error(e.message)
        }

    }

    override suspend fun updateShoppingItemDiscount(
        shoppingItemId: String,
        discount: Float
    ): Resource<Unit> {
        return try {
            dao.updateShoppingItemDiscount(shoppingItemId, discount)
            Resource.Success(Unit)
        }catch (e: Exception){
            Resource.Error(e.message)
        }
    }

    override fun getBillUnSyncData(syncStatus: SyncStatus): Flow<Resource<List<Bill>>> {
        return dao.getBillUnSyncData(syncStatus)
            .map { unSyncBills ->
                Resource.Success(data = unSyncBills.map { it.toDomain() }) as Resource<List<Bill>>
            }
            .onStart {
                emit(Resource.Loading())
            }
            .catch { e ->
                emit(Resource.Error(e.message))
            }
    }

    override suspend fun updateBillSyncStatus(
        billId: String,
        syncStatus: SyncStatus
    ): Resource<Unit> {
        return try {
            dao.updateBillSyncStatus(billId, syncStatus)
            Resource.Success(Unit)
        }catch (e: Exception){
            Resource.Error(e.message)
        }
    }

    override fun getProductUnSyncData(syncStatus: SyncStatus): Flow<Resource<List<Product>>> {
        return dao.getProductUnSyncData(syncStatus)
            .map { unSyncProduct ->
                Resource.Success(data = unSyncProduct.map { it.toProduct() }) as Resource<List<Product>>
            }
            .onStart {
                emit(Resource.Loading())
            }
            .catch { e ->
                emit(Resource.Error(e.message))
            }
    }

    override suspend fun updateProductSyncStatus(
        productId: String,
        syncStatus: SyncStatus
    ): Resource<Unit> {
        return try {
            dao.updateProductSyncStatus(productId, syncStatus)
            Resource.Success(Unit)
        }catch (e: Exception){
            Resource.Error(e.message)
        }
    }

    override fun getShoppingItemUnSyncData(syncStatus: SyncStatus): Flow<Resource<List<ShoppingDetails>>> {
        return dao.getShoppingItemUnSyncData(syncStatus)
            .map { unSyncItems ->
                Resource.Success(data = unSyncItems) as Resource<List<ShoppingDetails>>
            }
            .onStart {
                emit(Resource.Loading())
            }
            .catch { e ->
                emit(Resource.Error(e.message))
            }
    }

    override suspend fun updateShoppingItemSyncStatus(
        itemId: String,
        syncStatus: SyncStatus
    ): Resource<Unit> {
        return try {
            dao.updateShoppingItemSyncStatus(itemId, syncStatus)
            Resource.Success(Unit)
        }catch (e: Exception){
            Resource.Error(e.message)
        }
    }

    override suspend fun insertItemList(
        items: List<ShoppingItem>
    ): Resource<Unit> {
        return try {
            val entities = items.map { it.toEntity() }
            dao.insertItemList(entities)
            Resource.Success(Unit)
        }catch (e: Exception){
            Resource.Error(e.message)
        }
    }

    override suspend fun insertBillList(bills: List<Bill>): Resource<Unit> {
        return try {
            val entities = bills.map { it.toEntity() }
            dao.insertBillList(entities)
            Resource.Success(Unit)
        }catch (e: Exception){
            Resource.Error(e.message)
        }
    }

    override suspend fun insertProductList(products: List<Product>): Resource<Unit> {
        return try {
            val entities = products.map { it.toProductEntity() }
            dao.insertProductList(entities)
            Resource.Success(Unit)
        }catch (e: Exception){
            Resource.Error(e.message)
        }
    }


    override suspend fun insertProduct(product: Product):Resource<Unit>{
        return try {
            dao.insertProduct(product.toProductEntity())
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun deleteProduct(product: Product):Resource<Unit> {
        return try {
            dao.deleteProduct(product.toProductEntity())
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }


}