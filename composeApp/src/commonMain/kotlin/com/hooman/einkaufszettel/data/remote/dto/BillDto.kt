package com.hooman.einkaufszettel.data.remote.dto

import com.hooman.einkaufszettel.domain.model.Bill
import com.hooman.einkaufszettel.domain.model.PurchaseType
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.koin.core.qualifier.named

@Serializable
data class BillDto(
    val billDateMillis: Long = 0L,
    val name: String = "",
    val userId: String = "",
    val type: String
){
    fun toDomain(id: String): Bill = Bill(
        id = id,
        billDate = Instant.fromEpochMilliseconds(billDateMillis),
        items = emptyList(),
        name = name,
        userId = userId,
        type = PurchaseType.valueOf(type)
    )

    companion object{
        fun fromDomain(bill: Bill): BillDto = BillDto(
            billDateMillis = bill.billDate.toEpochMilliseconds(),
            userId = bill.userId,
            name = bill.name,
            type = bill.type.name
        )
    }
}
