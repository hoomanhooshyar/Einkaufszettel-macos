package com.hooman.einkaufszettel.domain.source

import com.hooman.einkaufszettel.domain.model.Bill
import com.hooman.einkaufszettel.domain.model.Product
import com.hooman.einkaufszettel.domain.model.ShoppingItem
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.firestore.CollectionReference
import dev.gitlive.firebase.firestore.FirebaseFirestore

interface FirebaseService {
    val auth: FirebaseAuth
    val db: FirebaseFirestore

    fun billsCol(): CollectionReference
    fun productsCol(): CollectionReference
    fun shoppingItemsCol(billId: String): CollectionReference

    suspend fun <T> io(block: suspend () -> T): T
    suspend fun requiredUserId(): String
    suspend fun <T> safe(block: suspend () -> T): Result<T>
}