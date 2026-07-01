package com.hooman.einkaufszettel.feature.presentation.report.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.hooman.einkaufszettel.core.presentation.AppDimens
import com.hooman.einkaufszettel.core.presentation.whiteColor

@Composable
fun ReportCard(
    modifier: Modifier = Modifier,
    backgroundColor: Brush,
    title: String,
    value: String,
    textColor: Color = whiteColor
) {
    Card(
        modifier = modifier
            .background(Color.Transparent)
            .padding(AppDimens.spacingExtraSmall),
        shape = RoundedCornerShape(AppDimens.cardRadiusMedium),
        elevation = CardDefaults.cardElevation(
            defaultElevation = AppDimens.cardElevation
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ){
        Box(
            modifier = Modifier
                .background(
                    brush = backgroundColor,
                    shape = RoundedCornerShape(AppDimens.cardRadiusMedium)
                    )
                .fillMaxWidth()
                .padding(vertical = AppDimens.spacingMedium, horizontal = AppDimens.spacingSmall)
        ){
            Column(
                verticalArrangement = Arrangement.Center
            ){
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    color = textColor
                )
            }
        }
    }
}