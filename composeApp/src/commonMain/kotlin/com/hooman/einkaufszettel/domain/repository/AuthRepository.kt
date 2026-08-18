package com.hooman.einkaufszettel.domain.repository

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getCurrentUserId(): String?

    suspend fun signInWithGoogle(idToken: String, accessToken: String?):Resource<User>
    suspend fun getCurrentUser(): User?
    suspend fun signOut(): Resource<Unit>
}