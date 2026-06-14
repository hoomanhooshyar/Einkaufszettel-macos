package com.hooman.einkaufszettel.feature.presentation.shopping_item_list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hooman.einkaufszettel.core.presentation.AppDimens
import com.hooman.einkaufszettel.core.presentation.redColor
import com.hooman.einkaufszettel.core.presentation.whiteColor

@Composable
fun CountValue(
    modifier: Modifier = Modifier,
    count: Int,
    plusColor: Brush,
    minusColor: Brush,
    onUpdate: (Int) -> Unit
) {
    val componentHeight = 36.dp
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ){
        Box(
            modifier = Modifier
                .size(componentHeight)
                .background(
                    brush = minusColor,
                    shape = RoundedCornerShape(AppDimens.cardRadiusMedium),
                )
                .border(
                    width = 1.dp,
                    color = whiteColor,
                    shape = RoundedCornerShape(AppDimens.cardRadiusMedium)
                )
                .clickable{
                    if(count > 0){

                        onUpdate(count - 1)
                    }
                },
            contentAlignment = Alignment.Center
        ){
            Icon(
                imageVector = Icons.Default.Remove,
                tint = whiteColor,
                contentDescription = "Decrease",
                modifier = Modifier.size(20.dp)
            )
        }

        BasicTextField(
            modifier = Modifier
                .width(55.dp)
                .height(45.dp)
                .padding(AppDimens.spacingSmall)
                .border(
                    width = 1.dp,
                    color = whiteColor,
                    shape = RoundedCornerShape(AppDimens.cardRadiusMedium)
                ),
            decorationBox = {innerTextField ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    innerTextField()
                }
            },
            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.Center,
                color = whiteColor,
                fontWeight = FontWeight.Bold
            ),
            value = count.toString(),
            onValueChange = { newValue ->
                val parseCount = if(newValue.isEmpty()) 0 else newValue.toIntOrNull() ?: count
                onUpdate(parseCount)
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            singleLine = true,
            maxLines = 1,

        )

        Box(
            modifier = Modifier
                .size(componentHeight)
                .background(
                    brush = plusColor,
                    shape = RoundedCornerShape(AppDimens.cardRadiusMedium),
                )
                .border(
                    width = 2.dp,
                    color = whiteColor,
                    shape = RoundedCornerShape(AppDimens.cardRadiusMedium)
                )
                .clickable{
                    onUpdate(count + 1)
                },
            contentAlignment = Alignment.Center
        ){
            Icon(
                imageVector = Icons.Default.Add,
                tint = whiteColor,
                contentDescription = "Increase",
                modifier = Modifier.size(20.dp)
            )
        }
    }
}