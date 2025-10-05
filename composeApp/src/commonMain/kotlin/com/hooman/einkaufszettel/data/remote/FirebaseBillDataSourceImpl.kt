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
            svc.billsCol().add(BillDto.fromDomain(bill))
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