package com.hooman.einkaufszettel.domain.repository

import com.hooman.einkaufszettel.domain.model.User

interface AuthRepository {
    fun getCurrentUserId(): String?

    suspend fun signInWithGoogle(idToken: String): User
    suspend fun getCurrentUser(): User?
    suspend fun signOut()
}