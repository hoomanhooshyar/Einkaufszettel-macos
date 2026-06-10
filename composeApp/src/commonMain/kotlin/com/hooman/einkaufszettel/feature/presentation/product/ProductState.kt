package com.hooman.einkaufszettel.feature.presentation.product

import com.hooman.einkaufszettel.core.presentation.UiText
import com.hooman.einkaufszettel.domain.model.Product

data class ProductState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = true,
    val error: UiText? = null
)
