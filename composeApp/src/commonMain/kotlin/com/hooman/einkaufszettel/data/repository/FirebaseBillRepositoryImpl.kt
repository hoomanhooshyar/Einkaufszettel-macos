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
    override fun insertBill(bill: Bill): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())

        try {
            dataSource.insertBill(bill)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
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

    override fun deleteBill(billId: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            dataSource.deleteBill(billId)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }
}