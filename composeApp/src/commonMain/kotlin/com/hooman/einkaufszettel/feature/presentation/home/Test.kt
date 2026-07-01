package com.hooman.einkaufszettel.feature.presentation.home

import com.hooman.einkaufszettel.data.remote.dto.BillDto
import com.hooman.einkaufszettel.data.remote.dto.ProductDto
import com.hooman.einkaufszettel.data.remote.dto.ShoppingItemDto
import com.hooman.einkaufszettel.domain.model.Bill
import com.hooman.einkaufszettel.domain.model.Product
import com.hooman.einkaufszettel.domain.model.PurchaseType
import com.hooman.einkaufszettel.domain.model.ShoppingItem
import com.hooman.einkaufszettel.domain.source.FirebaseService
import kotlinx.datetime.Clock
import kotlin.collections.mapOf

fun generateTestBills(): List<Bill> {
    val testBills = mutableListOf<Bill>()
    val now = Clock.System.now()

    for (i in 1..10) {
        val type = when (i % 5) {
            0 -> PurchaseType.PARTY
            1 -> PurchaseType.HOUSE
            2 -> PurchaseType.FRIENDS
            3 -> PurchaseType.CLOTH
            else -> PurchaseType.SUPERMARKET
        }

        val items = List(3) { j ->
            ShoppingItem(
                id = "item_${i}_$j",
                billId = i.toString(),
                productId = "prod_${j}",
                itemCount = (1..5).random(),
                productName = "Product ${(1..100).random()}",
                productPrice = (1..50).random().toDouble(),
                productImage = null,
                userId = "test_user",
                discount = 0f,
                isChecked = false
            )
        }

        val bill = Bill(
            id = i.toString(),
            billDate = now, // هر bill مربوط به روز قبل‌تر
            items = items,
            userId = "test_user",
            name = "Bill $i",
            type = type
        )

        testBills.add(bill)
    }

    return testBills
}

suspend fun addBill(svc: FirebaseService){
    val uid = svc.requiredUserId()
    repeat(3){
        val bill = Bill(
            id = "",
            billDate = Clock.System.now(),
            items = emptyList(),
            userId = uid,
            name = "test",
            type = PurchaseType.PARTY
        )
        val billId = svc.billsCol().add(BillDto.fromDomain(bill))
        repeat(2){
            val item = ShoppingItem(
                id = "",
                billId = billId.id,
                productId = "test",
                itemCount = 1,
                productName = "test",
                productPrice = 1.0,
                productImage = null,
                userId = uid,
                discount = 0f,
                isChecked = false
            )

            svc.shoppingItemsCol(billId.id).add(ShoppingItemDto.fromDomain(item))
        }
    }

}

suspend fun addProduct(svc: FirebaseService){
    val uid = svc.requiredUserId()

    repeat(2){
        val product = Product(
            id = "",
            name = "test",
            image = null,
            price = 1.0,
            userId = uid
        )
        svc.productsCol().add(ProductDto.fromDomain(product))
    }
}