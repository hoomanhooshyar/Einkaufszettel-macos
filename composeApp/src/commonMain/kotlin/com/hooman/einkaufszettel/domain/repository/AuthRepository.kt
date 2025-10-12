package com.hooman.einkaufszettel.domain.repository

interface AuthRepository {
    fun getCurrentUserId(): String?
}