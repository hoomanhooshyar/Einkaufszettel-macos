package com.hooman.einkaufszettel.feature.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hooman.einkaufszettel.core.presentation.AppDimens
import com.hooman.einkaufszettel.core.presentation.blackColor
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
            .padding(AppDimens.spacingSmall)
            .clickable(onClick = onClick)
            ,
        shape = RoundedCornerShape(AppDimens.cardRadiusLarge),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ){
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = background)
                .padding(vertical = 32.dp)
        ){
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ){
                Text(
                    text = bill.billDate.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = whiteColor
                )
                Text(

                    text = bill.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = whiteColor,
                    fontWeight = FontWeight.Bold
                )
                Text(

                    text = items,
                    style = MaterialTheme.typography.bodyMedium,
                    color = whiteColor
                )

            }
            Card(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 8.dp, top = 16.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = whiteColor.copy(alpha = 0.5f)
                )

            ){
                Icon(
                    modifier = Modifier.padding(8.dp),
                    imageVector = icon,
                    contentDescription = null,
                    tint = blackColor
                )
            }

            IconButton(
                modifier = Modifier

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