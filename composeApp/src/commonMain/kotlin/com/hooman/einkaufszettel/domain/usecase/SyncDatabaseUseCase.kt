package com.hooman.einkaufszettel.domain.usecase

import com.hooman.einkaufszettel.domain.repository.SyncRepository

class SyncDatabaseUseCase(
    private val syncRepository: SyncRepository
) {
     suspend operator fun invoke(){
        syncRepository.syncDatabase()
    }
}