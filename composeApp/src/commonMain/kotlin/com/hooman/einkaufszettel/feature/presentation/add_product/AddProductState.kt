package com.hooman.einkaufszettel.feature.presentation.add_product

import com.hooman.einkaufszettel.core.presentation.UiText
import com.hooman.einkaufszettel.domain.model.Product

data class AddProductState(
    val isLoading: Boolean = false,
    val error: UiText? = null,
    val product: Product? = null,
    val productIcons: List<String> = emptyList(),
    val oldProduct: Product? = null //این رو اضافه کردم
)
