package com.hooman.einkaufszettel.feature.presentation.add_shopping_item

import com.hooman.einkaufszettel.core.presentation.UiText
import com.hooman.einkaufszettel.domain.model.Product

data class AddShoppingItemState(
    val isLoading: Boolean = false,
    val error: UiText? = null,
    val products: List<Product> = emptyList(),
    val checkedProductIds: Set<String> = emptySet()
)
