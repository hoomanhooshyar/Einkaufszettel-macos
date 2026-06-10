package com.hooman.einkaufszettel.domain.repository

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.Result
import com.hooman.einkaufszettel.domain.model.Bill
import kotlinx.coroutines.flow.Flow

interface FirebaseBillRepository {
    suspend fun insertBill(bill: Bill): Resource<Unit>
    fun getAllBillsByUserId(userId: String): Flow<Resource<List<Bill>>>
    fun getBillById(billId: String): Flow<Resource<Bill>>
    suspend fun deleteBill(billId: String): Resource<Unit>
}