package com.hooman.einkaufszettel.feature.presentation.report.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.hooman.einkaufszettel.core.presentation.AppDimens
import com.hooman.einkaufszettel.core.presentation.blackColor
import com.hooman.einkaufszettel.core.presentation.whiteColor
import com.hooman.einkaufszettel.feature.presentation.report.CategoryReport
import com.hooman.einkaufszettel.feature.presentation.report.ChartGuide
import einkaufszettel.composeapp.generated.resources.Res
import einkaufszettel.composeapp.generated.resources.no_data
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

@Composable
fun DonutChartCard(
    modifier: Modifier = Modifier,
    cardRadius: Dp = AppDimens.cardRadiusMedium,
    backgroundColor : Brush,
    titleChart: String,
    data: List<CategoryReport>
) {



    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = AppDimens.spacingSmall, vertical = AppDimens.spacingExtraSmall),
        shape = RoundedCornerShape(cardRadius),
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
                .background(backgroundColor)
                .padding(AppDimens.spacingSmall)
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ){
                if(data.isEmpty()){
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.5f),
                        contentAlignment = Alignment.Center
                    ){
                        Text(
                            text = stringResource(Res.string.no_data),
                            color = whiteColor,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }else{
                    val max = data.maxOf { it.percentage }

                    val formattedMax = (max * 100.0).roundToInt() / 100.0f
                    val guides: List<ChartGuide> = data.map { cat ->
                        ChartGuide(
                            title = cat.categoryName,
                            value = "${cat.percentage}",
                            color = cat.color
                        )
                    }
                    Text(
                        modifier = Modifier
                            .padding(AppDimens.spacingMedium)
                            .fillMaxWidth(),
                        text = titleChart,
                        color = blackColor,
                        fontSize = 16.sp
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        DonutChart(
                            modifier = Modifier.weight(1f),
                            data = data,
                            centerText = "$formattedMax"
                        )
                        ChartGuide(
                            modifier = Modifier.weight(1f),
                            guides = guides
                        )
                    }
                }

            }
        }

    }
}