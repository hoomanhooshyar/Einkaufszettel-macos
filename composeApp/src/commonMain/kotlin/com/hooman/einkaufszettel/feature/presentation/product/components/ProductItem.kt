package com.hooman.einkaufszettel.feature.presentation.product.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hooman.einkaufszettel.core.presentation.AppDimens

import com.hooman.einkaufszettel.core.presentation.whiteColor
import com.hooman.einkaufszettel.domain.model.Product
import com.hooman.einkaufszettel.feature.presentation.components.ProductImage


@Composable
fun ProductItem(
    product: Product,
    onProductClick: (productId: String) -> Unit,
    onDeleteClick: (product: Product) -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Brush
) {
    val imageSize = 50.dp
    val spacerWidth = 16.dp
    Card(
        modifier = modifier
            .padding(horizontal = AppDimens.spacingSmall, vertical = AppDimens.spacingSmall),
        shape = RoundedCornerShape(AppDimens.cardRadiusLarge),
        elevation = CardDefaults.cardElevation(
            defaultElevation = AppDimens.cardElevation
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        onClick = {onProductClick(product.id)}
    ) {
        Box(
            modifier = Modifier
                .background(brush = backgroundColor)
                .padding(vertical = 24.dp)
                .fillMaxWidth()


        ){
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                ProductImage(
                    modifier = Modifier
                        .padding(vertical = AppDimens.extraSpacing, horizontal = AppDimens.spacingMedium)
                        .size(imageSize),
                    imageUrl = product.image ?:  "noimage"

                )
                Spacer(
                    modifier = Modifier.width(spacerWidth)
                )
                Column(
                    modifier = Modifier.weight(1f)
                ){
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = whiteColor,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${product.price} €",
                        style = MaterialTheme.typography.titleLarge,
                        color = whiteColor,
                        fontWeight = FontWeight.Bold
                    )
                }


                IconButton(
                    onClick = { onDeleteClick(product) },
                ){
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = whiteColor
                    )
                }
            }
        }

    }
}