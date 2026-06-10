package com.hooman.einkaufszettel.feature.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hooman.einkaufszettel.core.presentation.AppDimens
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
                        .background(brush = background)
                ){
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ){
                        CheckBoxItem(
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
                        .background(brush = background)
                ){
                    Row(
                        modifier = Modifier.fillMaxWidth()
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