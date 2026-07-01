package com.hooman.einkaufszettel.data.repository

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.model.User
import com.hooman.einkaufszettel.domain.repository.AuthRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.AuthResult
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.GoogleAuthProvider
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthRepositoryImpl(
    private val auth: FirebaseAuth
): AuthRepository {
    override fun getCurrentUserId(): String? {
        return Firebase.auth.currentUser?.uid
    }

    override suspend fun signInWithGoogle(idToken: String, accessToken: String?):Resource<User> {
        return try {
            val credential = GoogleAuthProvider.credential(idToken,accessToken)
            val currentUser = auth.currentUser
            val result = if(currentUser != null && currentUser.isAnonymous){
                currentUser.linkWithCredential(credential)
            }else{
                auth.signInWithCredential(credential)
            }
            val u = result.user ?: return Resource.Error("Firebase user is null")
            val user = User(
                id = u.uid,
                name = u.displayName,
                imageUrl = u.photoURL
            )
            Resource.Success(data = user)
        }catch (e: Exception){
            e.printStackTrace()
            Resource.Error(message = e.message ?: "Unknown Error")
        }
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