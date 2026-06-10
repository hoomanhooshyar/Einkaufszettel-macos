package com.hooman.einkaufszettel.feature.presentation.create_bill

import com.hooman.einkaufszettel.core.presentation.UiText
import com.hooman.einkaufszettel.domain.model.Bill

data class CreateBillState(
    val isLoading: Boolean = false,
    val error: UiText? = null
)
