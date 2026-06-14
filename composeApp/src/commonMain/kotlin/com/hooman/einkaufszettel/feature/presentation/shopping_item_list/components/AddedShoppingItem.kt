package com.hooman.einkaufszettel.feature.presentation.shopping_item_list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hooman.einkaufszettel.core.presentation.AppDimens
import com.hooman.einkaufszettel.core.presentation.whiteColor
import com.hooman.einkaufszettel.domain.model.ShoppingDetails
import com.hooman.einkaufszettel.feature.presentation.components.CheckBoxItem

@Composable
fun AddedShoppingItem(
    modifier: Modifier = Modifier,
    item: ShoppingDetails,
    background: Brush,
    isChecked: Boolean,
    plusColor: Brush,
    minusColor: Brush,
    onDeleteClick: (String) -> Unit,
    onCountChange: (String, Int) -> Unit,
    onCheckedChange: (String, Boolean) -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(AppDimens.cardRadiusLarge),
        elevation = CardDefaults.cardElevation(
            defaultElevation = AppDimens.cardElevation
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ){
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(background)
        ){
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = AppDimens.cardVerticalPadding),
                verticalAlignment = Alignment.CenterVertically
            ){
                CheckBoxItem(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = AppDimens.spacingSmall),

                    text = item.productName,
                    isChecked = isChecked,
                    onCheckedChange = { newValue ->
                        onCheckedChange(item.shoppingItemId, newValue)
                    },
                    image = item.productImage,
                    textColor = whiteColor,

                )

                CountValue(
                    modifier = Modifier.padding(end = 8.dp),
                    count = item.itemCount ?: 0,
                    plusColor = plusColor,
                    minusColor = minusColor,
                    onUpdate = { newCount ->
                        onCountChange(item.shoppingItemId, newCount)
                    }
                )
            }

            IconButton(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = AppDimens.spacingSmall),
                onClick = {
                    onDeleteClick(item.shoppingItemId)
                }
            ){
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "delete",
                    tint = whiteColor
                )
            }
        }
    }
}