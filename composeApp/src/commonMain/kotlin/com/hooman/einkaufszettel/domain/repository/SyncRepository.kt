package com.hooman.einkaufszettel.domain.repository

interface SyncRepository {

    suspend fun syncDatabase()
}