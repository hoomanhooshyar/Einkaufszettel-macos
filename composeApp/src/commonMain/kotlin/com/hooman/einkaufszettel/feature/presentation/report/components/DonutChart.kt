package com.hooman.einkaufszettel.feature.presentation.report.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hooman.einkaufszettel.core.presentation.blackColor
import com.hooman.einkaufszettel.feature.presentation.report.CategoryReport

@Composable
fun DonutChart(
    data: List<CategoryReport>,
    modifier: Modifier = Modifier,
    strockWidth: Float = 50f,
    centerText: String = ""
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ){
        Canvas(
            modifier = Modifier.fillMaxSize()
        ){
            var startAngle = -90f

            data.forEach { category ->
                val sweepAngle = category.percentage * 360 // Convert percentage to degrees

                //Arc drawing
                drawArc(
                    color = category.color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(
                        width = strockWidth,
                        cap = StrokeCap.Butt //Make the edges flat and stock together
                    )
                )

                startAngle += sweepAngle // Update Angle for next Color

            }
        }

        //Write Center Text. Like 48%
        if(centerText.isNotEmpty()){
            Text(
                text = centerText,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = blackColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}