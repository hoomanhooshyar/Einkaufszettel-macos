package com.hooman.einkaufszettel.data.repository

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.data.local.dao.AppDao
import com.hooman.einkaufszettel.data.mapper.toDomain
import com.hooman.einkaufszettel.data.mapper.toEntity
import com.hooman.einkaufszettel.data.mapper.toProduct
import com.hooman.einkaufszettel.data.mapper.toProductEntity
import com.hooman.einkaufszettel.domain.model.Bill
import com.hooman.einkaufszettel.domain.model.Product
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

    override fun insertBill(bill: Bill): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            dao.insertBill(bill.toEntity())
            emit(Resource.Success(Unit))
        }catch (e: Exception){
            emit(Resource.Error(e.message))
        }
    }

    override fun deleteBill(bill: Bill): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            dao.deleteBill(bill.toEntity())
            emit(Resource.Success(Unit))
        }catch (e: Exception){
            emit(Resource.Error(e.message))
        }
    }

    override fun insertShoppingItem(
        shoppingItem: ShoppingItem,
        billId: String
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            dao.insertShoppingItem(shoppingItem.toEntity())
            emit(Resource.Success(Unit))
        }catch (e: Exception){
            emit(Resource.Error(e.message))
        }
    }

    override fun deleteShoppingItem(
        shoppingItem: ShoppingItem,
        billId: String
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            dao.deleteShoppingItem(shoppingItem.toEntity())
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }

    override fun getAllProducts(): Flow<Resource<List<Product>>> = flow {
        emit(Resource.Loading())
        try {
            dao.getAllProducts().collect { products ->
                emit(Resource.Success(data = products.map { it.toProduct() }))
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

    override fun insertProduct(product: Product): Flow<Resource<Unit>> = flow{
        emit(Resource.Loading())
        try {
            dao.insertProduct(product.toProductEntity())
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }

    override fun deleteProduct(product: Product): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            dao.deleteProduct(product.toProductEntity())
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }
}