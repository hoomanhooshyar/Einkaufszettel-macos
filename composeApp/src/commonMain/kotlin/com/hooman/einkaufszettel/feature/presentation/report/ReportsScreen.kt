package com.hooman.einkaufszettel.feature.presentation.report

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hooman.einkaufszettel.core.presentation.AppDimens
import com.hooman.einkaufszettel.core.presentation.backgroundGradient
import com.hooman.einkaufszettel.core.presentation.greenGradient
import com.hooman.einkaufszettel.core.presentation.naturalGrayGradient
import com.hooman.einkaufszettel.core.presentation.orangeGradient
import com.hooman.einkaufszettel.core.presentation.purpleGradient
import com.hooman.einkaufszettel.core.presentation.redGradient
import com.hooman.einkaufszettel.core.presentation.whiteColor
import com.hooman.einkaufszettel.feature.presentation.components.CETextField
import com.hooman.einkaufszettel.feature.presentation.report.components.BarChartCard
import com.hooman.einkaufszettel.feature.presentation.report.components.CustomDatePicker
import com.hooman.einkaufszettel.feature.presentation.report.components.CustomFilterTab
import com.hooman.einkaufszettel.feature.presentation.report.components.DonutChartCard
import com.hooman.einkaufszettel.feature.presentation.report.components.ReportCard
import com.hooman.einkaufszettel.feature.utils.DateTime
import einkaufszettel.composeapp.generated.resources.Res
import einkaufszettel.composeapp.generated.resources.average_per_purchase
import einkaufszettel.composeapp.generated.resources.count_of_items
import einkaufszettel.composeapp.generated.resources.discount
import einkaufszettel.composeapp.generated.resources.total_amount
import einkaufszettel.composeapp.generated.resources.total_purchase_by_percent
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.days

@Composable
fun ReportsScreenRoot(
    viewModel: ReportsViewModel = koinViewModel(),
    contentPadding: PaddingValues
) {
    val reportState by viewModel.reportState.collectAsState()
    ReportsScreen(
        contentPadding = contentPadding,
        onDateSelectorClick = { timeFilter, startDate, endDate ->

            viewModel.getBillsByDate(
                timeFilter = timeFilter,
                customStartDate = startDate,
                customEndDate = endDate
            )
        },
        barChartData = reportState.barChartReport,
        categories = reportState.categoryReports,
        totalAmount = reportState.totalAmount,
        itemCount = reportState.purchaseCount,
        averagePerPurchase = reportState.averagePerPurchase,
        discount = reportState.totalDiscount
    )
}

@Composable
fun ReportsScreen(
    contentPadding: PaddingValues,
    onDateSelectorClick: (TimeFilter, Instant, Instant) -> Unit,
    barChartData: List<BarChartReport>,
    categories: List<CategoryReport>,
    totalAmount: Double = 0.0,
    itemCount: Int = 0,
    averagePerPurchase: Double = 0.0,
    discount: Double = 0.0
) {

    val filterItems = listOf("Woche", "Monat", "Jahr", "Custom")
    var selectedIndex by remember { mutableStateOf(1) }
    val date = DateTime.getFormattedDate(Clock.System.now().toString())
    var showDateTextField by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    val endDate = Clock.System.now()
    var savedIndex by remember { mutableStateOf(1) }
    var selectedEndDate by remember { mutableStateOf(Clock.System.now()) }
    var selectedStartDate by remember { mutableStateOf(Clock.System.now()) }
    val scrollState = rememberScrollState()
    val formattedTotalAmount = (totalAmount * 100).roundToInt() / 100.0f
    val formattedAveragePerPurchase = (averagePerPurchase * 100).roundToInt() / 100.0f
    val formattedDiscount = (discount * 100).roundToInt() / 100.0f
    LaunchedEffect(Unit) {

        onDateSelectorClick(TimeFilter.MONTH, endDate.minus(30.days), endDate)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .background(Color.Transparent)
                .verticalScroll(scrollState),

            ) {

            CustomFilterTab(
                tabs = filterItems,
                selectedTabIndex = selectedIndex,
                onTabClick = { index ->
                    if (index != 3) {
                        savedIndex = index
                    }
                    selectedIndex = index
                    when (index) {
                        0 -> {
                            showDateTextField = false
                            onDateSelectorClick(
                                TimeFilter.WEEK,
                                selectedEndDate.minus(7.days),
                                selectedEndDate
                            )
                        }

                        1 -> {
                            showDateTextField = false
                            onDateSelectorClick(
                                TimeFilter.MONTH,
                                selectedEndDate.minus(30.days),
                                selectedEndDate
                            )
                        }

                        2 -> {
                            showDateTextField = false
                            onDateSelectorClick(
                                TimeFilter.YEAR,
                                selectedEndDate.minus(365.days),
                                selectedEndDate
                            )
                        }

                        3 -> {
                            showDateTextField = true
                            selectedEndDate = Clock.System.now()
                            selectedStartDate = when (savedIndex) {
                                0 -> endDate.minus(7.days)
                                1 -> endDate.minus(30.days)
                                2 -> endDate.minus(365.days)
                                else -> endDate.minus(30.days)
                            }
                        }
                    }
                },
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
            )



            Text(
                modifier = Modifier.padding(start = AppDimens.spacingSmall),
                text = date,
                color = whiteColor,
                fontSize = 16.sp
            )
            if (showDateTextField) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        val startLocalDate =
                            selectedStartDate.toLocalDateTime(TimeZone.currentSystemDefault())
                        val formattedStartDate = "${
                            startLocalDate.dayOfMonth.toString().padStart(2, '0')
                        }.${
                            startLocalDate.monthNumber.toString().padStart(2, '0')
                        }.${startLocalDate.year}"


                        CETextField(
                            modifier = Modifier
                                .fillMaxWidth(),
                            value = formattedStartDate,
                            onValueChange = {},
                            label = {
                                Text(text = "Start Date")
                            },
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = null
                                )
                            },
                            keyboardType = KeyboardType.Text,
                            placeholder = {
                                Text(text = "Start Date")
                            }
                        )

                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(Color.Transparent)
                                .clickable(onClick = {
                                    showDatePicker = true
                                })
                        )
                    }

                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        val endLocalDate =
                            selectedEndDate.toLocalDateTime((TimeZone.currentSystemDefault()))
                        val formattedEndDate = "${
                            endLocalDate.dayOfMonth.toString().padStart(2, '0')
                        }.${
                            endLocalDate.monthNumber.toString().padStart(2, '0')
                        }.${endLocalDate.year}"
                        CETextField(
                            modifier = Modifier
                                .fillMaxWidth(),
                            value = formattedEndDate,
                            onValueChange = {},
                            label = {
                                Text(text = "End Date")
                            },
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = null
                                )
                            },
                            keyboardType = KeyboardType.Text,
                            placeholder = {
                                Text(text = "End Date")
                            }
                        )

                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(Color.Transparent)
                                .clickable(onClick = {
                                    showDatePicker = true
                                })
                        )
                    }


                }

                if (showDatePicker) {
                    CustomDatePicker(
                        onDismiss = {
                            showDatePicker = false
                        },
                        onDateSelected = { startDate, endDate ->
                            selectedStartDate = startDate
                            selectedEndDate = endDate
                            showDatePicker = false
                            onDateSelectorClick(
                                TimeFilter.CUSTOM,
                                startDate,
                                endDate
                            )

                        }
                    )
                }

            }
            Spacer(
                modifier = Modifier.height(16.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                ReportCard(
                    modifier = Modifier
                        .wrapContentHeight()
                        .weight(1f),
                    backgroundColor = greenGradient,
                    textColor = whiteColor,
                    title = stringResource(Res.string.total_amount),
                    value = "$formattedTotalAmount"
                )
                ReportCard(
                    modifier = Modifier
                        .wrapContentHeight()
                        .weight(1f),
                    backgroundColor = orangeGradient,
                    textColor = whiteColor,
                    title = stringResource(Res.string.count_of_items),
                    value = "$itemCount"
                )
            }
            Spacer(
                modifier = Modifier.height(8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                ReportCard(
                    modifier = Modifier
                        .wrapContentHeight()
                        .weight(1f),
                    backgroundColor = purpleGradient,
                    textColor = whiteColor,
                    title = stringResource(Res.string.average_per_purchase),
                    value = "$formattedAveragePerPurchase"
                )
                ReportCard(
                    modifier = Modifier
                        .wrapContentHeight()
                        .weight(1f),
                    backgroundColor = redGradient,
                    textColor = whiteColor,
                    title = stringResource(Res.string.discount),
                    value = "${formattedDiscount}€"
                )
            }
            Spacer(
                modifier = Modifier.height(24.dp)
            )
            DonutChartCard(
                modifier = Modifier
                    .fillMaxWidth(),
                backgroundColor = naturalGrayGradient,
                data = categories,
                titleChart = stringResource(Res.string.total_purchase_by_percent)
            )
            BarChartCard(
                modifier = Modifier
                    .fillMaxWidth(),
                backgroundColor = naturalGrayGradient,
                data = barChartData,
                titleChart = "Bar Chart"
            )
            Spacer(
                modifier = Modifier.height(24.dp)
            )
        }
    }
}