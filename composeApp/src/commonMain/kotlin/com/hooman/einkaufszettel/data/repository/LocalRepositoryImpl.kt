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
import com.hooman.einkaufszettel.domain.repository.AuthRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlin.time.ExperimentalTime

class LocalRepositoryImpl(
    private val dao: AppDao,
    private val authRepository: AuthRepository
): LocalRepository {
    private fun requireUserId(): String =
        authRepository.getCurrentUserId()?.takeIf { it.isNotBlank() }
            ?: error("Authentication required")

    private fun checkedOwner(owner: String, activeUserId: String = requireUserId()): String {
        require(owner.isBlank() || owner == activeUserId) { "Account ownership mismatch" }
        return activeUserId
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun <T> observeAccount(query: (String) -> Flow<T>): Flow<T> =
        authRepository.userId.distinctUntilChanged().flatMapLatest { userId ->
            query(userId.orEmpty())
        }

    override fun getAllBills(): Flow<Resource<List<Bill>>> {
        return observeAccount { userId -> dao.getAllBills(userId = userId) }
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
        return observeAccount { userId -> dao.getBillById(billId, userId = userId) }
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
        return observeAccount { userId -> dao.getBillByName(name, userId = userId) }
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
        return observeAccount { userId ->
            dao.getAllBillsByDate(
                startDate = startDate,
                endDate = endDate,
                userId = userId
            )
        }
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
            dao.insertBill(bill.toEntity().copy(userId = checkedOwner(bill.userId)))
            Resource.Success(Unit)
        }catch (e: Exception){
            if (e is CancellationException) throw e
            Resource.Error(e.message)
        }
    }

    override suspend fun deleteBill(bill: Bill): Resource<Unit> {
        return try {
            dao.deleteBill(bill.id, checkedOwner(bill.userId))
            Resource.Success(Unit)
        }catch (e: Exception){
            if (e is CancellationException) throw e
            Resource.Error(e.message)
        }
    }

    override suspend fun insertShoppingItem(
        shoppingItem: ShoppingItem,
        billId: String
    ):Resource<Unit> {
        return try {
            dao.insertShoppingItem(shoppingItem.toEntity().copy(userId = checkedOwner(shoppingItem.userId)))
            Resource.Success(Unit)
        }catch (e: Exception){
            if (e is CancellationException) throw e
            Resource.Error(e.message)
        }
    }

    override suspend fun deleteShoppingItem(
        shoppingItemId: String
    ):Resource<Unit>{
        return try {
            dao.deleteShoppingItem(shoppingItemId = shoppingItemId, userId = requireUserId())
            Resource.Success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Resource.Error(e.message)
        }
    }

    override suspend fun deleteShoppingItemByProductAndBill(
        billId: String,
        productId: String
    ): Resource<Unit> {
        return try {
            dao.deleteShoppingItemByProductAndBill(billId, productId, userId = requireUserId())
            Resource.Success(Unit)
        }catch (e: Exception){
            if (e is CancellationException) throw e
            Resource.Error(e.message)
        }
    }

    override fun getAllProducts(): Flow<Resource<List<Product>>> {
        return observeAccount { userId -> dao.getAllProducts(userId = userId) }
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
        return observeAccount { userId -> dao.getProductByName(name, userId = userId) }
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
        return observeAccount { userId -> dao.getProductById(productId, userId = userId) }
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
        return observeAccount { userId -> dao.getProductIcons(userId = userId) }
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
        return observeAccount { userId -> dao.getShoppingItemsByBillId(billId, userId = userId) }
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
        return observeAccount { userId -> dao.getAvailableProductsForShoppingItem(billId, userId = userId) }
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
        return observeAccount { userId -> dao.getCheckedProductsForShoppingItem(billId, userId = userId) }
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
        return observeAccount { userId -> dao.getProductsForShoppingItem(billId, userId = userId) }
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
            dao.updateShoppingItemCheckStatus(shoppingItemId, isChecked, userId = requireUserId())
            Resource.Success(Unit)
        }catch (e: Exception){
            if (e is CancellationException) throw e
            Resource.Error(e.message)
        }
    }

    override suspend fun updateSHoppingItemCount(
        shoppingItemId: String,
        itemCount: Int
    ):Resource<Unit> {
        return try {
            dao.updateShoppingItemCount(shoppingItemId, itemCount, userId = requireUserId())
            Resource.Success(Unit)
        }catch (e: Exception){
            if (e is CancellationException) throw e
            Resource.Error(e.message)
        }

    }

    override suspend fun updateShoppingItemDiscount(
        shoppingItemId: String,
        discount: Float
    ): Resource<Unit> {
        return try {
            dao.updateShoppingItemDiscount(shoppingItemId, discount, userId = requireUserId())
            Resource.Success(Unit)
        }catch (e: Exception){
            if (e is CancellationException) throw e
            Resource.Error(e.message)
        }
    }

    override fun getBillUnSyncData(syncStatus: SyncStatus): Flow<Resource<List<Bill>>> {
        return observeAccount { userId -> dao.getBillUnSyncData(syncStatus, userId = userId) }
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
            dao.updateBillSyncStatus(billId, syncStatus, userId = requireUserId())
            Resource.Success(Unit)
        }catch (e: Exception){
            if (e is CancellationException) throw e
            Resource.Error(e.message)
        }
    }

    override fun getProductUnSyncData(syncStatus: SyncStatus): Flow<Resource<List<Product>>> {
        return observeAccount { userId -> dao.getProductUnSyncData(syncStatus, userId = userId) }
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
            dao.updateProductSyncStatus(productId, syncStatus, userId = requireUserId())
            Resource.Success(Unit)
        }catch (e: Exception){
            if (e is CancellationException) throw e
            Resource.Error(e.message)
        }
    }

    override fun getShoppingItemUnSyncData(syncStatus: SyncStatus): Flow<Resource<List<ShoppingDetails>>> {
        return observeAccount { userId -> dao.getShoppingItemUnSyncData(syncStatus, userId = userId) }
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
            dao.updateShoppingItemSyncStatus(itemId, syncStatus, userId = requireUserId())
            Resource.Success(Unit)
        }catch (e: Exception){
            if (e is CancellationException) throw e
            Resource.Error(e.message)
        }
    }

    override suspend fun insertItemList(
        items: List<ShoppingItem>
    ): Resource<Unit> {
        return try {
            val userId = requireUserId()
            val entities = items.map {
                require(it.userId == userId) { "Account ownership mismatch" }
                it.toEntity().copy(syncStatus = SyncStatus.SUCCESS)
            }
            dao.insertItemList(entities)
            Resource.Success(Unit)
        }catch (e: Exception){
            if (e is CancellationException) throw e
            Resource.Error(e.message)
        }
    }

    override suspend fun insertBillList(bills: List<Bill>): Resource<Unit> {
        return try {
            val userId = requireUserId()
            val entities = bills.map {
                require(it.userId == userId) { "Account ownership mismatch" }
                it.toEntity().copy(syncStatus = SyncStatus.SUCCESS)
            }
            dao.insertBillList(entities)
            Resource.Success(Unit)
        }catch (e: Exception){
            if (e is CancellationException) throw e
            Resource.Error(e.message)
        }
    }

    override suspend fun insertProductList(products: List<Product>): Resource<Unit> {
        return try {
            val userId = requireUserId()
            val entities = products.map {
                require(it.userId == userId) { "Account ownership mismatch" }
                it.toProductEntity().copy(syncStatus = SyncStatus.SUCCESS)
            }
            dao.insertProductList(entities)
            Resource.Success(Unit)
        }catch (e: Exception){
            if (e is CancellationException) throw e
            Resource.Error(e.message)
        }
    }


    override suspend fun insertProduct(product: Product):Resource<Unit>{
        return try {
            dao.insertProduct(product.toProductEntity().copy(userId = checkedOwner(product.userId)))
            Resource.Success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Resource.Error(e.message)
        }
    }

    override suspend fun deleteProduct(product: Product):Resource<Unit> {
        return try {
            dao.deleteProduct(product.id, checkedOwner(product.userId))
            Resource.Success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Resource.Error(e.message)
        }
    }



    override suspend fun acknowledgeBill(uploaded: Bill, status: SyncStatus): Boolean {
        require(uploaded.userId == requireUserId()) { "Account ownership mismatch" }
        return dao.acknowledgeBill(uploaded.toEntity(), status)
    }


    override suspend fun acknowledgeProduct(uploaded: Product, status: SyncStatus): Boolean {
        require(uploaded.userId == requireUserId()) { "Account ownership mismatch" }
        return dao.acknowledgeProduct(uploaded.toProductEntity(), status)
    }


    override suspend fun acknowledgeShoppingItem(uploaded: ShoppingItem, status: SyncStatus): Boolean {
        require(uploaded.userId == requireUserId()) { "Account ownership mismatch" }
        return dao.acknowledgeShoppingItem(uploaded.toEntity(), status)
    }

}
