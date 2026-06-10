package com.hooman.einkaufszettel.data.repository

import com.hooman.einkaufszettel.core.util.Resource

import com.hooman.einkaufszettel.domain.model.Bill
import com.hooman.einkaufszettel.domain.repository.FirebaseBillRepository
import com.hooman.einkaufszettel.domain.source.FirebaseBillDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

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

    override fun getAllBillsByUserId(userId: String): Flow<Resource<List<Bill>>> = flow {
        emit(Resource.Loading())
        try {
            dataSource.getAllBillsByUserId(userId).collect { bills ->
                emit(Resource.Success(bills))
            }

        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }

    override fun getBillById(billId: String): Flow<Resource<Bill>> = flow{
        emit(Resource.Loading())
        try {
            dataSource.getBillById(billId).collect { bill ->
                emit(Resource.Success(bill))
            }
        } catch (e: Exception) {
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