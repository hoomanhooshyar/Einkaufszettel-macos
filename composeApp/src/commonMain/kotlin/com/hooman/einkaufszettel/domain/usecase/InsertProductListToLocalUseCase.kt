package com.hooman.einkaufszettel.domain.usecase

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.model.Product
import com.hooman.einkaufszettel.domain.repository.LocalRepository

class InsertProductListToLocalUseCase(
    private val repository: LocalRepository
) {
    suspend operator fun invoke(products: List<Product>): Resource<Unit>{
        return repository.insertProductList(products)
    }
}