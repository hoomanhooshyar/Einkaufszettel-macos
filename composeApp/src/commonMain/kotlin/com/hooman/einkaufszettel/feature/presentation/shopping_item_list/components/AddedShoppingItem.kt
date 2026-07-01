package com.hooman.einkaufszettel.feature.presentation.shopping_item_list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hooman.einkaufszettel.core.presentation.AppDimens
import com.hooman.einkaufszettel.core.presentation.animations.DisintegratingItemWrapper
import com.hooman.einkaufszettel.core.presentation.animations.animatedSwipedBackground
import com.hooman.einkaufszettel.core.presentation.whiteColor
import com.hooman.einkaufszettel.domain.model.ShoppingDetails
import com.hooman.einkaufszettel.feature.presentation.components.CETextField
import com.hooman.einkaufszettel.feature.presentation.components.CheckBoxItem
import einkaufszettel.composeapp.generated.resources.Res
import einkaufszettel.composeapp.generated.resources.discount
import org.jetbrains.compose.resources.stringResource

@Composable
fun AddedShoppingItem(
    modifier: Modifier = Modifier,
    item: ShoppingDetails,
    selectedBackground: Brush,
    unselectedBackground: Brush,
    isChecked: Boolean,
    plusColor: Brush,
    minusColor: Brush,
    onDeleteClick: (String) -> Unit,
    onCountChange: (String, Int) -> Unit,
    onCheckedChange: (String, Boolean) -> Unit,
    onDiscountChange:(String, Float) -> Unit
) {
    var discount by remember { mutableStateOf(item.discount) }

    var isRemoving by remember { mutableStateOf(false) }

    DisintegratingItemWrapper(
        isRemoving = isRemoving,
        onAnimationComplete = {
            onDeleteClick(item.shoppingItemId)
        }
    ){
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
                    .animatedSwipedBackground(
                        isChecked = isChecked,
                        selectedBrush = selectedBackground,
                        unselectedBrush = unselectedBackground
                    )
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
                        isRemoving = true
                    }
                ){
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "delete",
                        tint = whiteColor
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(top = 140.dp),
                    horizontalArrangement = Arrangement.Center
                ){
                    Text(
                        text = stringResource(Res.string.discount),
                        color = whiteColor,
                        fontSize = 16.sp,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(AppDimens.spacingSmall))
                    BasicTextField(
                        modifier = Modifier
                            .padding(bottom = 12.dp)
                            .width(50.dp)
                            .height(25.dp)
                            .border(
                                width = 2.dp,
                                color = whiteColor,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        value = discount.toString(),
                        onValueChange = { newDiscount ->
                            discount = newDiscount.toFloatOrNull() ?: 0f
                            onDiscountChange(item.shoppingItemId, discount)
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        maxLines = 1,
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(
                            color = whiteColor,
                            textAlign = TextAlign.Center
                        ),
                        readOnly = item.isChecked

                    )

                    Spacer(modifier = Modifier.width(AppDimens.spacingSmall))

                    Text(
                        text = "%",
                        color = whiteColor,
                        fontSize = 16.sp,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

            }
        }
    }
}