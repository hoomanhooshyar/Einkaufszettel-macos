package com.hooman.einkaufszettel.feature.presentation.report.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hooman.einkaufszettel.core.presentation.AppDimens
import com.hooman.einkaufszettel.core.presentation.blackColor
import com.hooman.einkaufszettel.domain.model.PurchaseType
import com.hooman.einkaufszettel.feature.presentation.report.ChartGuide
import kotlin.math.roundToInt

@Composable
fun ChartGuide(
    modifier: Modifier = Modifier,
    guides: List<ChartGuide>,
    textColor: Color = blackColor,
    dotSize: Dp = 16.dp
) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ){
             guides.forEach { guide ->
                 val formattedValue = (guide.value.toFloat() * 100.0).roundToInt() / 100.0f
                 val icon = when(guide.title){
                     PurchaseType.SUPERMARKET.name -> PurchaseType.SUPERMARKET.icon
                     PurchaseType.PARTY.name -> PurchaseType.PARTY.icon
                     PurchaseType.HOUSE.name -> PurchaseType.HOUSE.icon
                     PurchaseType.FRIENDS.name -> PurchaseType.FRIENDS.icon
                     PurchaseType.CLOTH.name -> PurchaseType.CLOTH.icon
                     else -> PurchaseType.OTHER.icon

                 }
                 Row(
                     modifier = Modifier
                         .fillMaxWidth()
                         .padding(horizontal = AppDimens.spacingSmall),
                     verticalAlignment = Alignment.CenterVertically
                 ) {
                    if(guide.color != null){
                        Box(
                            modifier = Modifier
                                .size(dotSize)
                                .background(
                                    color = guide.color,
                                    shape = CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                     Icon(
                         modifier = Modifier.padding(8.dp),
                         imageVector = icon,
                         contentDescription = null,
                         tint = blackColor
                     )
                     Text(
                         text = "${formattedValue}",
                         color = textColor,
                         maxLines = 1,
                         overflow = TextOverflow.Ellipsis
                     )

                 }
             }

        }

}