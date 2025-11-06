package com.hooman.einkaufszettel.feature.presentation.product.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.Image
import coil3.compose.rememberAsyncImagePainter
import com.hooman.einkaufszettel.core.presentation.whiteColor
import com.hooman.einkaufszettel.domain.model.Product
import einkaufszettel.composeapp.generated.resources.Res
import einkaufszettel.composeapp.generated.resources.noimage
import org.jetbrains.compose.resources.painterResource

@Composable
fun ProductItem(
    product: Product,
    onProductClick: (product: Product) -> Unit,
    onDeleteClick: (product: Product) -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Brush
) {
    Card(
        modifier = modifier
            .clickable(onClick = {onProductClick(product)})
            .padding(8.dp),
        shape = RoundedCornerShape(32.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = backgroundColor)
        ){
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Image(
                    painter = if(product.image != null) rememberAsyncImagePainter(product.image) else painterResource(
                        Res.drawable.noimage),
                    contentDescription = product.name,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .padding(vertical = 16.dp, horizontal = 12.dp),
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier
                        .weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally

                ){
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = whiteColor
                    )
                    Text(
                        text = "${product.price} €",
                        style = MaterialTheme.typography.bodyMedium,
                        color = whiteColor
                    )
                }

                IconButton(
                    onClick = { onDeleteClick(product) },
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
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