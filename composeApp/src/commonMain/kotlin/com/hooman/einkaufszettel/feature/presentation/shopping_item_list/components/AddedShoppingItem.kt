package com.hooman.einkaufszettel.feature.presentation.shopping_item_list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hooman.einkaufszettel.core.presentation.AppDimens
import com.hooman.einkaufszettel.core.presentation.blackColor
import com.hooman.einkaufszettel.core.presentation.whiteColor
import com.hooman.einkaufszettel.domain.model.ShoppingDetails
import com.hooman.einkaufszettel.feature.presentation.components.CETextField
import com.hooman.einkaufszettel.feature.presentation.components.CheckBoxItem

@Composable
fun AddedShoppingItem(
    modifier: Modifier = Modifier,
    item: ShoppingDetails,
    background: Brush,
    isChecked: Boolean,
    onCheckedChange: (String, Boolean) -> Unit,
    onCountChange: (String, Int) -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(AppDimens.cardRadius),
        elevation = CardDefaults.cardElevation(
            defaultElevation = AppDimens.cardElevation
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ){
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(background)
        ){
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ){
                CheckBoxItem(
                    modifier = Modifier.padding(horizontal = AppDimens.spacingSmall),
                    text = item.productName,
                    isChecked = isChecked,
                    onCheckedChange = { newValue ->
                        onCheckedChange(item.shoppingItemId,newValue)
                    },
                    image = item.productImage,
                    textColor = whiteColor
                )

                BasicTextField(
                    modifier = Modifier
                        .width(64.dp)
                        .padding(vertical = 8.dp)
                        .border(1.dp, whiteColor, shape = RoundedCornerShape(AppDimens.cardRadius)),
                    textStyle = LocalTextStyle.current.copy(
                        textAlign = TextAlign.Center,
                        color = whiteColor
                    ),
                    value = item.itemCount.toString(),
                    onValueChange = { count ->
                        val parseCount = count.toIntOrNull() ?: 0
                        onCountChange(item.shoppingItemId, parseCount)
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true,
                    maxLines = 1
                )
            }
        }
    }
}