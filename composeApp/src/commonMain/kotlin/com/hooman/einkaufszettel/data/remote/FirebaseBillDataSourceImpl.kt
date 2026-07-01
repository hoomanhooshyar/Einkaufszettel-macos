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
            // ۱. ساخت یک عملیات گروهی (Batch)
            val batch = svc.db.batch()

            // ۲. پیدا کردن تمام آیتم‌های داخل این فاکتور
            // نکته: در کتابخانه KMP فایربیس، get() خودش suspend است.
            val itemSnapshot = svc.shoppingItemsCol(billId).get()

            // ۳. اضافه کردن دستور حذف تک‌تک آیتم‌ها به Batch
            for(doc in itemSnapshot.documents){
                batch.delete(doc.reference)
            }

            // ۴. اضافه کردن دستور حذفِ خودِ فاکتور به Batch
            val billRef = svc.billsCol().document(billId)
            batch.delete(billRef)

            // ۵. شلیک نهایی! اجرای تمام عملیات‌ها به صورت یکپارچه
            batch.commit()
        }
    }

}