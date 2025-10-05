package com.hooman.einkaufszettel.data.mapper

import com.hooman.einkaufszettel.data.local.entity.BillEntity
import com.hooman.einkaufszettel.domain.model.Bill

fun Bill.toEntity(): BillEntity {
    return BillEntity(
        id = id,
        billDate = billDate
    )
}

fun BillEntity.toDomain(): Bill {
    return Bill(
        id = id,
        billDate = billDate,
        userId = "",
        items = emptyList()
    )
}