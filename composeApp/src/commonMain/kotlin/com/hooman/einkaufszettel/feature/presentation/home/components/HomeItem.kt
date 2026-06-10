package com.hooman.einkaufszettel.feature.presentation.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hooman.einkaufszettel.core.presentation.AppDimens
import com.hooman.einkaufszettel.core.presentation.blackColor
import com.hooman.einkaufszettel.core.presentation.greenColor
import com.hooman.einkaufszettel.core.presentation.whiteColor
import com.hooman.einkaufszettel.domain.model.Bill

@Composable
fun HomeItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onDeleteClick: (Bill) -> Unit,
    bill: Bill,
    background: Brush,
    icon: ImageVector
) {

    val items = if(bill.items.isEmpty())
            ""
        else if(bill.items.size == 1)
            bill.items[0].productName
        else
            "${bill.items[0].productName}, ${bill.items[1].productName}"

    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(AppDimens.cardRadius),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
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
            Column(
                modifier = Modifier.padding(16.dp)
            ){
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = bill.billDate.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = whiteColor
                )
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = bill.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = whiteColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = items,
                    style = MaterialTheme.typography.bodyMedium,
                    color = whiteColor
                )

            }
            Card(
                modifier = Modifier
                    .padding(end = 16.dp, bottom = 16.dp)
                    .align(Alignment.BottomEnd),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = whiteColor.copy(alpha = 0.5f)
                )

            ){
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(8.dp),
                    tint = blackColor
                )
            }

            IconButton(
                modifier = Modifier
                    .padding(
                        top = AppDimens.spacingLarge,
                        end = AppDimens.spacingMedium
                    )
                    .align(Alignment.TopEnd),
                onClick = {onDeleteClick(bill)}
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