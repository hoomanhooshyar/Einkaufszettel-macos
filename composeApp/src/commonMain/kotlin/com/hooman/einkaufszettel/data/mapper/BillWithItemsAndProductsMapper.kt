package com.hooman.einkaufszettel.data.mapper

import com.hooman.einkaufszettel.data.local.relation.BillWithItemsAndProducts
import com.hooman.einkaufszettel.domain.model.Bill
import com.hooman.einkaufszettel.domain.model.ShoppingItem

fun BillWithItemsAndProducts.toDomain(): Bill{
    return Bill(
        id = bill.id,
        billDate = bill.billDate,
        userId = "",
        items = items.map { shoppingItemWithProduct ->
            ShoppingItem(
                id = shoppingItemWithProduct.item.id,
                productId = shoppingItemWithProduct.product.id,
                productName = shoppingItemWithProduct.product.name,
                itemCount = shoppingItemWithProduct.item.itemCount,
                productPrice = shoppingItemWithProduct.product.price,
                productImage = shoppingItemWithProduct.product.image,
                billId = bill.id,
                userId = ""
            )
        }
    )
}