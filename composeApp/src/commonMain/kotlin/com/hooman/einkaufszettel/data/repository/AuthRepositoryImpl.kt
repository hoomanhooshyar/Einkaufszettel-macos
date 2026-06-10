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

    override suspend fun signInWithGoogle(idToken: String, accessToken: String?): Flow<Resource<User>> = flow {
//        val credential = GoogleAuthProvider.credential(idToken,null)
//        val result: AuthResult? = auth.signInWithCredential(credential)
//        if(result == null)
//            emit(Resource.Error("Sign in failed"))
//        else{
//            val u = result.user ?: error("Firebase user is null")
//            val user = User(
//                id = u.uid,
//                name = u.displayName,
//                imageUrl = u.photoURL
//            )
//            emit(Resource.Success(user))
//        }
        try {
            val credential = GoogleAuthProvider.credential(idToken,accessToken)
            val currentUser = auth.currentUser
            val result: AuthResult

            if(currentUser != null && currentUser.isAnonymous){
                result = currentUser.linkWithCredential(credential)
            }else{
                result = auth.signInWithCredential(credential)
            }
            val u = result.user ?: error("Firebase user is null")
            val user = User(
                id = u.uid,
                name = u.displayName,
                imageUrl = u.photoURL
            )
            emit(Resource.Success(user))
        }catch (e: Exception){
            e.printStackTrace()
            emit(Resource.Error(e.message.toString()))
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