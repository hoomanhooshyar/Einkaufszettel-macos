package com.hooman.einkaufszettel.data.remote

import com.hooman.einkaufszettel.domain.source.FirebaseService
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.CollectionReference
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class FirebaseServiceImpl() : FirebaseService {
    override val auth: FirebaseAuth get() = Firebase.auth
    override val db: FirebaseFirestore get() = Firebase.firestore
    override fun billsCol(): CollectionReference = db.collection("bills")

    override fun productsCol(): CollectionReference = db.collection("products")


    override fun shoppingItemsCol(billId: String): CollectionReference = billsCol().document(billId).collection("shoppingItems")
    override fun productAssetsCol(): CollectionReference = db.collection("product_icon")

    override suspend fun <T> io(block: suspend () -> T): T = withContext(Dispatchers.IO) { block() }

    override suspend fun requiredUserId(): String //= auth.currentUser?.uid ?: error("User not logged in")
        {
            auth.currentUser?.uid?.let { return it }

            val result = auth.signInAnonymously()
            val uid = result.user?.uid

            return uid ?: error("User not logged in")
        }


    override suspend fun <T> safe(block: suspend () -> T): Result<T> {
        return try {
            Result.success(block())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}