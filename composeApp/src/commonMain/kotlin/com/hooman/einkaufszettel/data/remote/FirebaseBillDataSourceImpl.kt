package com.hooman.einkaufszettel.data.remote

import com.hooman.einkaufszettel.data.remote.dto.BillDto
import com.hooman.einkaufszettel.domain.model.Bill
import com.hooman.einkaufszettel.domain.source.FirebaseBillDataSource
import com.hooman.einkaufszettel.domain.source.FirebaseService
import dev.gitlive.firebase.firestore.where
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FirebaseBillDataSourceImpl(
    private val svc: FirebaseService
): FirebaseBillDataSource {
    override suspend fun insertBill(bill: Bill) {
        svc.io {
            try {
                println("🟢 DEBUG: Try to save to Firestore with ID: ${bill.id}")
                svc.billsCol()
                    .document(bill.id)
                    .set(BillDto.fromDomain(bill))
                println("🟢 DEBUG: Successfully saved to Firestore!")
            }catch (e: Exception){
                println("🔴 DEBUG: Firebase CRASHED: ${e.message}")
                e.printStackTrace()
            }

        }
    }

    override fun getAllBillsByUserId(userId: String): Flow<List<Bill>> =
         svc.billsCol()
             .where { "userId" equalTo   userId }
             .snapshots
             .map { snapshot ->
                 snapshot.documents.map { doc ->
                     doc.data<BillDto>().toDomain(doc.id)
                 }
             }




    override fun getBillById(billId: String): Flow<Bill?> =
        svc.billsCol()
            .document(billId)
            .snapshots
            .map { doc ->
                doc.data<BillDto>().toDomain(doc.id)
            }


    override suspend fun deleteBill(billId: String) {
        svc.io {
            svc.billsCol().document(billId).delete()
        }
    }

}