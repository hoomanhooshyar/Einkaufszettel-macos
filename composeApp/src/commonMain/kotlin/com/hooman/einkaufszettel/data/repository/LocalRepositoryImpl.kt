package com.hooman.einkaufszettel.data.repository

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.data.local.dao.AppDao
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
import kotlinx.coroutines.flow.flow

class LocalRepositoryImpl(
    private val dao: AppDao
): LocalRepository {
    override fun getAllBills(): Flow<Resource<List<Bill>>> = flow {
        emit(Resource.Loading())
        try {
            dao.getAllBills().collect { bills ->
               emit(Resource.Success(data = bills.map{it.toDomain()}))
             }
        }catch (e: Exception){
            emit(Resource.Error(e.message))
        }
    }

    override fun getBillById(billId: String): Flow<Resource<Bill>> = flow {
        emit(Resource.Loading())
        try {
            dao.getBillById(billId).collect { bill ->
                emit(Resource.Success(data = bill?.toDomain()))
            }
        }catch (e: Exception){
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

    override fun getAllProducts(): Flow<Resource<List<Product>>> = flow {
        emit(Resource.Loading())
        try {
            dao.getAllProducts().collect { products ->
                emit(Resource.Success(data = products.map {
                    it.toProduct()
                }
                ))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }

    override fun getProductByName(name: String): Flow<Resource<List<Product>>> = flow{
        emit(Resource.Loading())
        try {
            dao.getProductByName(name).collect { products ->
                emit(Resource.Success(data = products.map { it.toProduct() }))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }

    override fun getProductById(productId: String): Flow<Resource<Product>> = flow {
        emit(Resource.Loading())
        try {
            dao.getProductById(productId).collect { product ->
                emit(Resource.Success(data = product?.toProduct()))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }

    override fun getProductIcons(): Flow<Resource<List<String>>> = flow {
        emit(Resource.Loading())
        try {

        }catch (e: Exception){
            emit(Resource.Error(e.message))
        }
    }

    override fun getAllShoppingItemsByBillId(billId: String): Flow<Resource<List<ShoppingItem>>> = flow{
        emit(Resource.Loading())
        try {
            dao.getShoppingItemsByBillId(billId).collect { shoppingItems ->
                emit(Resource.Success(data = shoppingItems.map { it.toShoppingItem() }))
            }
        }catch (e: Exception){
            emit(Resource.Error(e.message))
        }
    }

    override fun getAvailableProductsForShoppingItem(billId: String): Flow<Resource<List<Product>>> = flow{
        emit(Resource.Loading())
        try {
            dao.getAvailableProductsForShoppingItem(billId).collect { products ->
                emit(Resource.Success(data = products.map { it.toProduct() }))
            }
        }catch (e: Exception){
            emit(Resource.Error(e.message))
        }
    }

    override fun getCheckedProductsForShoppingItem(billId: String): Flow<Resource<List<String>>> = flow{
        emit(Resource.Loading())
        try {
            dao.getCheckedProductsForShoppingItem(billId).collect { products ->
                emit(Resource.Success(data = products))
            }
        }catch (e: Exception){
            emit(Resource.Error(e.message))
        }

    }

    override fun getProductsForShoppingItem(billId: String): Flow<Resource<List<ShoppingDetails>>> = flow{
        emit(Resource.Loading())
        try {
            dao.getProductsForShoppingItem(billId).collect { shoppingDetails ->
                emit(Resource.Success(data = shoppingDetails))
            }
        }catch (e: Exception){
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