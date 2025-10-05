package com.hooman.einkaufszettel.data.remote.dto

import com.hooman.einkaufszettel.domain.model.Bill
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class BillDto(
    val billDateMillis: Long = 0L,
    val userId: String = ""
){
    fun toDomain(id: String): Bill = Bill(
        id = id,
        billDate = Instant.fromEpochMilliseconds(billDateMillis),
        items = emptyList(),
        userId = userId
    )

    companion object{
        fun fromDomain(bill: Bill): BillDto = BillDto(
            billDateMillis = bill.billDate.toEpochMilliseconds(),
            userId = bill.userId
        )
    }
}
