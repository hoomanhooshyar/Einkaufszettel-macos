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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.Alignment
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.datetime.LocalDate
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
import com.hooman.einkaufszettel.core.util.toTwoDecimals
import com.hooman.einkaufszettel.feature.presentation.components.CETextField
import com.hooman.einkaufszettel.feature.presentation.report.components.BarChartCard
import com.hooman.einkaufszettel.feature.presentation.report.components.CustomDatePicker
import com.hooman.einkaufszettel.feature.presentation.report.components.CustomFilterTab
import com.hooman.einkaufszettel.feature.presentation.report.components.DonutChartCard
import com.hooman.einkaufszettel.feature.presentation.report.components.ReportCard
import com.hooman.einkaufszettel.feature.utils.DateTime
import einkaufszettel.composeapp.generated.resources.Res
import einkaufszettel.composeapp.generated.resources.report_week
import einkaufszettel.composeapp.generated.resources.report_month
import einkaufszettel.composeapp.generated.resources.report_year
import einkaufszettel.composeapp.generated.resources.report_custom
import einkaufszettel.composeapp.generated.resources.report_start_date
import einkaufszettel.composeapp.generated.resources.report_end_date
import einkaufszettel.composeapp.generated.resources.report_retry
import einkaufszettel.composeapp.generated.resources.report_bar_chart
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
    val state by viewModel.reportState.collectAsStateWithLifecycle()
    ReportsScreen(
        contentPadding = contentPadding,
        state = state,
        onFilterSelected = { viewModel.getBillsByDate(it) },
        onCustomRangeSelected = { start, end ->
            viewModel.getBillsByDate(TimeFilter.CUSTOM, start, end)
        },
        onRetry = viewModel::retry
    )
}

@Composable
fun ReportsScreen(
    contentPadding: PaddingValues,
    state: ReportState,
    onFilterSelected: (TimeFilter) -> Unit,
    onCustomRangeSelected: (LocalDate, LocalDate) -> Unit,
    onRetry: () -> Unit
) {
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    if (showDatePicker) {
        CustomDatePicker(
            initialStartDate = state.customStartDate ?: state.startDate,
            initialEndDate = state.customEndDate ?: state.endDate,
            onDismiss = { showDatePicker = false },
            onDateSelected = { start, end ->
                showDatePicker = false
                onCustomRangeSelected(start, end)
            }
        )
    }
    Column(
        modifier = Modifier.fillMaxSize().background(backgroundGradient)
            .padding(contentPadding).verticalScroll(rememberScrollState())
    ) {
        CustomFilterTab(
            tabs = listOf(
                stringResource(Res.string.report_week),
                stringResource(Res.string.report_month),
                stringResource(Res.string.report_year),
                stringResource(Res.string.report_custom)
            ),
            selectedTabIndex = TimeFilter.entries.indexOf(state.selectedTimeFilter),
            onTabClick = { index ->
                val filter = TimeFilter.entries[index]
                if (filter == TimeFilter.CUSTOM) showDatePicker = true
                else onFilterSelected(filter)
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Text(
            text = state.dateRangeText,
            color = whiteColor,
            fontSize = 16.sp,
            modifier = Modifier.padding(horizontal = AppDimens.spacingSmall)
        )
        if (state.selectedTimeFilter == TimeFilter.CUSTOM) {
            Row(modifier = Modifier.fillMaxWidth()) {
                ReportDateField(
                    modifier = Modifier.weight(1f),
                    label = stringResource(Res.string.report_start_date),
                    value = state.startDate?.toReportDateText().orEmpty(),
                    onClick = { showDatePicker = true }
                )
                ReportDateField(
                    modifier = Modifier.weight(1f),
                    label = stringResource(Res.string.report_end_date),
                    value = state.endDate?.toReportDateText().orEmpty(),
                    onClick = { showDatePicker = true }
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        val error = state.error
        when {
            state.isLoading -> Box(
                modifier = Modifier.fillMaxWidth().padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = whiteColor)
            }
            error != null -> {
                Text(
                    text = error.asString(),
                    color = whiteColor,
                    modifier = Modifier.padding(AppDimens.spacingSmall)
                )
                TextButton(onClick = onRetry) { Text(stringResource(Res.string.report_retry), color = whiteColor) }
            }
            else -> ReportContent(state)
        }
    }
}

@Composable
private fun ReportDateField(
    label: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = whiteColor,
                unfocusedTextColor = whiteColor,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedBorderColor = whiteColor,
                unfocusedBorderColor = whiteColor
            ),
            value = value,
            onValueChange = {},
            label = { Text(label, color = whiteColor) },
            placeholder = { Text(label, color = whiteColor) },
            readOnly = true,
            trailingIcon = {
                Icon(Icons.Default.DateRange, contentDescription = label, tint = whiteColor)
            }
        )
        Box(Modifier.matchParentSize().clickable(onClick = onClick))
    }
}

@Composable
private fun ReportContent(state: ReportState) {
    Column {
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
                    value = "${state.totalAmount.toTwoDecimals()} €"
                )
                ReportCard(
                    modifier = Modifier
                        .wrapContentHeight()
                        .weight(1f),
                    backgroundColor = orangeGradient,
                    textColor = whiteColor,
                    title = stringResource(Res.string.count_of_items),
                    value = "${state.purchaseCount}"
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
                    value = "${state.averagePerPurchase.toTwoDecimals()} €"
                )
                ReportCard(
                    modifier = Modifier
                        .wrapContentHeight()
                        .weight(1f),
                    backgroundColor = redGradient,
                    textColor = whiteColor,
                    title = stringResource(Res.string.discount),
                    value = "${state.totalDiscount.toTwoDecimals()} €"
                )
            }
            Spacer(
                modifier = Modifier.height(24.dp)
            )
            if (state.categoryReports.isNotEmpty()) DonutChartCard(
                modifier = Modifier
                    .fillMaxWidth(),
                backgroundColor = naturalGrayGradient,
                data = state.categoryReports,
                titleChart = stringResource(Res.string.total_purchase_by_percent)
            )
            BarChartCard(
                modifier = Modifier
                    .fillMaxWidth(),
                backgroundColor = naturalGrayGradient,
                data = state.barChartReport,
                titleChart = stringResource(Res.string.report_bar_chart)
            )
            Spacer(
                modifier = Modifier.height(24.dp)
            )
    }
}
