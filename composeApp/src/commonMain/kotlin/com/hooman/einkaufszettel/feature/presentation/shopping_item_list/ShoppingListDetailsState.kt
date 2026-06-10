package com.hooman.einkaufszettel.feature.presentation.shopping_item_list

import com.hooman.einkaufszettel.core.presentation.UiText
import com.hooman.einkaufszettel.domain.model.Bill
import com.hooman.einkaufszettel.domain.model.ShoppingDetails

data class ShoppingListDetailsState(
    val isLoading: Boolean = false,
    val shoppingDetailsItems: List<ShoppingDetails>? = emptyList(),
    val bill: Bill? = null,
    val error: UiText? = null
)
