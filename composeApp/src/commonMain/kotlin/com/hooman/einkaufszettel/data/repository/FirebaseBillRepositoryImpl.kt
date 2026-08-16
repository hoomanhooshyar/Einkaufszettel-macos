package com.hooman.einkaufszettel.data.repository

import com.hooman.einkaufszettel.core.util.Resource

import com.hooman.einkaufszettel.domain.model.Bill
import com.hooman.einkaufszettel.domain.repository.FirebaseBillRepository
import com.hooman.einkaufszettel.domain.source.FirebaseBillDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class FirebaseBillRepositoryImpl(
    private val dataSource: FirebaseBillDataSource
): FirebaseBillRepository {
    override suspend fun insertBill(bill: Bill): Resource<Unit> {
        return try {
            dataSource.insertBill(bill)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }

    }

    override fun getAllBillsByUserId(userId: String): Flow<Resource<List<Bill>>> {
        return dataSource.getAllBillsByUserId(userId)
            .map { bills ->
                Resource.Success(bills) as Resource<List<Bill>>
            }
            .onStart {
                emit(Resource.Loading())
            }
            .catch { e ->
                emit(Resource.Error(e.message))
            }
    }

    override fun getBillById(billId: String): Flow<Resource<Bill>>{
       return dataSource.getBillById(billId)
           .map { bill ->
               Resource.Success(bill) as Resource<Bill>
           }
           .onStart {
               emit(Resource.Loading())
           }
           .catch { e ->
               emit(Resource.Error(e.message))
           }

    }

    override fun getBillByName(name: String): Flow<Resource<List<Bill>>> {
        return dataSource.getBillByName(name)
            .map { bills ->
                Resource.Success(bills) as Resource<List<Bill>>
            }
            .onStart {
                emit(Resource.Loading())
            }
            .catch { e ->
                emit(Resource.Error(e.message))
            }
    }

    override suspend fun deleteBill(billId: String): Resource<Unit> {
        return try {
            dataSource.deleteBill(billId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }
}