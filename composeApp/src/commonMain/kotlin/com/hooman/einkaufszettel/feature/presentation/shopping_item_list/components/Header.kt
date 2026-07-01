package com.hooman.einkaufszettel.feature.presentation.shopping_item_list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.hooman.einkaufszettel.core.presentation.orangeGradient
import com.hooman.einkaufszettel.core.presentation.whiteColor
import com.hooman.einkaufszettel.core.util.toTwoDecimals
import com.hooman.einkaufszettel.domain.model.Bill
import com.hooman.einkaufszettel.domain.model.getDisplayTypename
import com.hooman.einkaufszettel.feature.presentation.components.CEButton
import com.hooman.einkaufszettel.feature.utils.DateTime
import einkaufszettel.composeapp.generated.resources.Res
import einkaufszettel.composeapp.generated.resources.add_item
import einkaufszettel.composeapp.generated.resources.bill_name
import einkaufszettel.composeapp.generated.resources.bill_type
import einkaufszettel.composeapp.generated.resources.count_of_items
import einkaufszettel.composeapp.generated.resources.created_date
import einkaufszettel.composeapp.generated.resources.total_amount
import org.jetbrains.compose.resources.stringResource

@Composable
fun Header(
    modifier: Modifier = Modifier,
    bill: Bill,
    background: Brush,
    totalAmount: Double = 0.0,
    onClick: () -> Unit
) {

    val date = DateTime.getFormattedDate(bill.billDate.toString())
    val type = bill.type.getDisplayTypename()
    val name = bill.name
    val itemCount = bill.items.size

   /* bill.items.forEach { item ->
        val totalPrice = item.itemCount * item.productPrice
        val discount = (item.discount / 100) * totalPrice
        val finalPrice = totalPrice - discount
        totalAmount += finalPrice
    }*/
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                vertical = 16.dp,
                horizontal = 8.dp
            )
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(8.dp)
            )
            .background(
                brush = background,
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 2.dp,
                color = whiteColor,
                shape = RoundedCornerShape(8.dp)
            )
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = stringResource(Res.string.bill_name),
                        color = whiteColor
                    )
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = name,
                        color = whiteColor
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = stringResource(Res.string.created_date),
                        color = whiteColor
                    )

                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = date,
                        color = whiteColor
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = stringResource(Res.string.bill_type),
                        color = whiteColor
                    )

                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = type,
                        color = whiteColor
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = stringResource(Res.string.count_of_items),
                        color = whiteColor
                    )

                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = itemCount.toString(),
                        color = whiteColor
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = stringResource(Res.string.total_amount),
                        color = whiteColor
                    )
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = totalAmount.toTwoDecimals(),
                        color = whiteColor
                    )
                }

                CEButton(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = {
                        onClick()
                    },
                    icon = Icons.Filled.Add,
                    text = stringResource(Res.string.add_item),
                    containerColor = orangeGradient,
                    contentColor = whiteColor
                )

            }
        }
}