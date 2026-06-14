package com.hooman.einkaufszettel.data.remote

import com.hooman.einkaufszettel.data.remote.dto.ShoppingItemDto
import com.hooman.einkaufszettel.domain.model.ShoppingItem
import com.hooman.einkaufszettel.domain.source.FirebaseService
import com.hooman.einkaufszettel.domain.source.FirebaseShoppingItemDataSource
import dev.gitlive.firebase.firestore.where
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FirebaseShoppingItemDataSourceImpl(
    private val svc: FirebaseService
): FirebaseShoppingItemDataSource {
    override suspend fun insertItem(shoppingItem: ShoppingItem) {
        svc.io {
            svc.shoppingItemsCol(shoppingItem.billId)
                .document(shoppingItem.productId)
                .set(ShoppingItemDto.fromDomain(shoppingItem))
        }
    }

    override fun getAllItemsByUserId(userId: String): Flow<List<ShoppingItem>> {
        return svc.db.collectionGroup("shoppingItems")
            .where { "userId" equalTo userId }
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { doc ->
                    doc.data<ShoppingItemDto>().toDomain(doc.id)
                }
            }
    }

    override suspend fun deleteShoppingItem(billId: String, shoppingItemId: String) {
        svc.io {
            svc.shoppingItemsCol(billId).document(shoppingItemId).delete()
        }
    }

    override suspend fun deleteShoppingItemByProductAndBill(
        billId: String,
        productId: String
    ) {
        svc.io {
            svc.shoppingItemsCol(billId).document(productId).delete()
        }
    }

    override fun getShoppingItemByBillId(billId: String): Flow<List<ShoppingItem>> {
        return svc.shoppingItemsCol(billId)
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { doc ->
                    doc.data<ShoppingItemDto>().toDomain(doc.id)
                }
            }
    }

    override suspend fun updateShoppingItemCheckStatus(
        billId: String,
        itemId: String,
        isChecked: Boolean
    ) {
        svc.io {
            svc.shoppingItemsCol(billId)
                .document(itemId)
                .update(mapOf("isChecked" to isChecked))
        }
    }

    override suspend fun updateShoppingItemCount(
        billId: String,
        productId: String,
        itemCount: Int
    ) {
        svc.io {
            svc.shoppingItemsCol(billId)
                .document(productId)
                .update(mapOf("itemCount" to itemCount))
        }
    }
}