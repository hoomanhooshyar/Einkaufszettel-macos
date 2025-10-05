package com.hooman.einkaufszettel.domain.source

import com.hooman.einkaufszettel.domain.model.Bill
import kotlinx.coroutines.flow.Flow

interface FirebaseBillDataSource {
    suspend fun insertBill(bill: Bill)
    fun getAllBillsByUserId(userId: String): Flow<List<Bill>>
    fun getBillById(billId: String): Flow<Bill?>
    suspend fun deleteBill(billId: String)
}