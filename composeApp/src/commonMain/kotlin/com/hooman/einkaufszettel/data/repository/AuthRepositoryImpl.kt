package com.hooman.einkaufszettel.data.repository

import com.hooman.einkaufszettel.domain.model.User
import com.hooman.einkaufszettel.domain.repository.AuthRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.GoogleAuthProvider
import dev.gitlive.firebase.auth.auth

class AuthRepositoryImpl(
    private val auth: FirebaseAuth
): AuthRepository {
    override fun getCurrentUserId(): String? {
        return Firebase.auth.currentUser?.uid
    }

    override suspend fun signInWithGoogle(idToken: String): User {
        val credential = GoogleAuthProvider.credential(idToken,null)
        val result = auth.signInWithCredential(credential)
        val u = result.user ?: error("Firebase user is null")

        return User(
            id = u.uid,
            name = u.displayName,
            imageUrl = u.photoURL
        )

    }

    override suspend fun getCurrentUser(): User? {
        return auth.currentUser?.let { u ->
            User(
                id = u.uid,
                name = u.displayName,
                imageUrl = u.photoURL
            )
        }
    }

    override suspend fun signOut() {
        auth.signOut()
    }
}