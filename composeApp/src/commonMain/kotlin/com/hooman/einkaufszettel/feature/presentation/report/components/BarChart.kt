package com.hooman.einkaufszettel.feature.presentation.report.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hooman.einkaufszettel.feature.presentation.report.BarChartReport

@Composable
fun BarChart(
    modifier: Modifier = Modifier,
    data: List<BarChartReport>,
) {
    val textMeasure = rememberTextMeasurer()
    val textStyle = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)

    val scrollState = rememberScrollState()
    val itemWidth = 50.dp
    val totalCanvasWidth = itemWidth * data.size.coerceAtLeast(1)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
    ) {
        Canvas(
            modifier = Modifier
                .width(totalCanvasWidth)
                .fillMaxHeight()
        ) {
            if (data.isEmpty()) return@Canvas

            val maxValue = data.maxOf { it.value }.coerceAtLeast(1f)

            val textSpace = 30.dp.toPx() // Space between bars and text

            val availableHeight = size.height - textSpace // Available height for bars

            val sectionWidth =
                size.width / data.size // Spacing the Width of each bar and both sides space

            val barWidth = sectionWidth * 0.6f // Width of each bar
            val spacing = sectionWidth * 0.4f // Padding between bars

            data.forEachIndexed { index, report ->
                val barHeight = (report.value / maxValue) * availableHeight // Height of each bar
                val topLeftX =
                    (index * sectionWidth) + (spacing / 2f) // X-coordinate of the top-left corner
                val topLeftY = availableHeight - barHeight

                //drawing Bars
                drawRoundRect(
                    color = report.color,
                    topLeft = Offset(x = topLeftX, y = topLeftY),
                    size = Size(width = barWidth, height = barHeight),
                    cornerRadius = CornerRadius(x = 6.dp.toPx(), y = 6.dp.toPx())
                )

                //Write Text under each Bar
                val textLayoutResult = textMeasure.measure(
                    text = report.label,
                    style = textStyle
                )

                val textX = topLeftX + (barWidth / 2f) - (textLayoutResult.size.width / 2f)
                val textY = availableHeight + 8.dp.toPx()

                drawText(
                    textLayoutResult = textLayoutResult,
                    topLeft = Offset(x = textX, y = textY)
                )

            }
        }
    }

}