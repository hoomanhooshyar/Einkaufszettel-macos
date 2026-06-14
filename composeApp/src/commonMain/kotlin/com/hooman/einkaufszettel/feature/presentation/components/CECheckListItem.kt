package com.hooman.einkaufszettel.feature.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import com.hooman.einkaufszettel.core.presentation.AppDimens
import com.hooman.einkaufszettel.core.presentation.whiteColor
import com.hooman.einkaufszettel.domain.model.Product
import com.hooman.einkaufszettel.domain.model.ShoppingItem

@Composable
fun CECheckListItem(
    modifier: Modifier = Modifier,
    item: Any,
    background: Brush,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {

    when(item){
        is ShoppingItem -> {
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
                        .background(brush = background)
                ){
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = AppDimens.cardVerticalPadding),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        CheckBoxItem(
                            textColor = whiteColor,
                            text = item.productName,
                            isChecked = isChecked,
                            onCheckedChange = { newValue ->
                                onCheckedChange(newValue)
                            },
                            image = item.productImage
                        )
                    }
                }
            }
        }
        is Product ->{
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
                        .background(brush = background),
                ){
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = AppDimens.cardVerticalPadding),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        CheckBoxItem(
                            text = item.name,
                            isChecked = isChecked,
                            onCheckedChange = { newValue ->
                                onCheckedChange(newValue)
                            },
                            image = item.image
                        )
                    }
                }
            }
        }
    }

}