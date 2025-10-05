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
            svc.shoppingItemsCol(shoppingItem.billId).add(ShoppingItemDto.fromDomain(shoppingItem))
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

    override suspend fun deleteShoppingItem(billId: String, itemId: String) {
        svc.io {
            svc.shoppingItemsCol(billId).document(itemId).delete()
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
}