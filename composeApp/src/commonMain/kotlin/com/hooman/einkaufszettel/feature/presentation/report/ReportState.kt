package com.hooman.einkaufszettel.feature.presentation.report

import kotlinx.datetime.LocalDate
import androidx.compose.ui.graphics.Color
import com.hooman.einkaufszettel.core.presentation.UiText
import com.hooman.einkaufszettel.core.presentation.redColor

data class ReportState(
    val isLoading: Boolean = true,
    val error: UiText? = null,

    val selectedTimeFilter: TimeFilter = TimeFilter.MONTH,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val customStartDate: LocalDate? = null,
    val customEndDate: LocalDate? = null,
    val dateRangeText: String = "",
    val billCount: Int = 0,

    val totalAmount: Double = 0.0,
    val purchaseCount: Int = 0,
    val averagePerPurchase: Double = 0.0,
    val totalDiscount: Double = 0.0,

    val chartGuides: List<ChartGuide> = emptyList(),
    val categoryReports: List<CategoryReport> = emptyList(),
    val barChartReport: List<BarChartReport> = emptyList(),
    val recentTransactions: List<TransactionItem> = emptyList(),

)

enum class TimeFilter {
    WEEK, MONTH, YEAR, CUSTOM
}

data class CategoryReport(
    val categoryName: String,
    val percentage: Float,
    val color: Color
)
data class BarChartReport(
    val label: String,
    val value: Float,
    val color: Color = redColor
)

data class TransactionItem(
    val title: String,
    val amount: Double
)

data class ChartGuide(
    val title: String,
    val value: String,
    val color: Color? = null
)

internal fun LocalDate.toReportDateText(): String =
    "${dayOfMonth.toString().padStart(2, '0')}.${monthNumber.toString().padStart(2, '0')}.$year"
