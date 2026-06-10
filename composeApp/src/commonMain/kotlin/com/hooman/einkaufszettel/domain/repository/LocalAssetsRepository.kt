package com.hooman.einkaufszettel.domain.repository

import com.hooman.einkaufszettel.core.util.Resource
import kotlinx.coroutines.flow.Flow

interface LocalAssetsRepository {
    fun getProductIcons(): Flow<Resource<List<String>>>
}