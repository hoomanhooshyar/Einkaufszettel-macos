package com.hooman.einkaufszettel.data.repository

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.repository.LocalAssetsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LocalAssetsRepositoryImpl: LocalAssetsRepository {
    private fun getOfflineIcons(): List<String>{
        return listOf(
            "local_banana",
            "local_bread",
            "local_cheese",
            "local_hen",
            "local_milk",
            "local_orange",
            "local_paprika",
            "local_strawberry"
        )
    }

    override fun getProductIcons(): Flow<Resource<List<String>>> = flow {
        emit(Resource.Loading())
        try {
            emit(Resource.Success(getOfflineIcons()))
        }catch (e: Exception){
            emit(Resource.Error(e.message))
        }
    }
}