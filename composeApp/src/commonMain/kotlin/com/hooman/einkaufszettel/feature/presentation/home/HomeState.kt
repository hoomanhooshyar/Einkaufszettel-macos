package com.hooman.einkaufszettel.feature.presentation.home

import com.hooman.einkaufszettel.core.presentation.UiText
import com.hooman.einkaufszettel.domain.model.Bill

data class HomeState(
    val searchQuery: String? = null,
    val searchResult: List<Bill> = emptyList(),
    val bills: List<Bill> = emptyList(),
    val isLoading: Boolean = true,
    val totalAmount: Double = 0.0,
    val error: UiText? = null
)
