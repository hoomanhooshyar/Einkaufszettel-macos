package com.hooman.einkaufszettel.data.mapper

import com.hooman.einkaufszettel.data.local.entity.BillEntity
import com.hooman.einkaufszettel.domain.model.Bill
import com.hooman.einkaufszettel.domain.model.PurchaseType

fun Bill.toEntity(): BillEntity {
    return BillEntity(
        id = id,
        billDate = billDate,
        name = name,
        type = type.name,
        syncStatus = syncStatus
    )
}

fun BillEntity.toDomain(): Bill {
    return Bill(
        id = id,
        billDate = billDate,
        userId = "",
        items = emptyList(),
        name = name,
        type = PurchaseType.valueOf(type),
        syncStatus = syncStatus

    )
}