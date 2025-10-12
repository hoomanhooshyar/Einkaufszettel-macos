package com.hooman.einkaufszettel.data.repository

import com.hooman.einkaufszettel.domain.repository.AuthRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth

class AuthRepositoryImpl: AuthRepository {
    override fun getCurrentUserId(): String? {
        return Firebase.auth.currentUser?.uid
    }
}