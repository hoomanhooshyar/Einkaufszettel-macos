package com.hooman.einkaufszettel.feature.presentation.report

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hooman.einkaufszettel.core.presentation.UiText
import com.hooman.einkaufszettel.core.presentation.blueColor
import com.hooman.einkaufszettel.core.presentation.darkGreenColor
import com.hooman.einkaufszettel.core.presentation.darkYellowColor
import com.hooman.einkaufszettel.core.presentation.orangeColor
import com.hooman.einkaufszettel.core.presentation.purpleColor
import com.hooman.einkaufszettel.core.presentation.redColor
import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.model.Bill
import com.hooman.einkaufszettel.domain.model.PurchaseType
import com.hooman.einkaufszettel.domain.usecase.GetBillsByDateFromLocalUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class ReportsViewModel(
    private val getBillL: GetBillsByDateFromLocalUseCase
) : ViewModel() {
    private data class Selection(
        val filter: TimeFilter,
        val startDate: LocalDate,
        val endDate: LocalDate,
        val timeZone: TimeZone,
        val customStartDate: LocalDate? = null,
        val customEndDate: LocalDate? = null,
        val revision: Long = 0
    )

    private val selection = MutableStateFlow(presetSelection(TimeFilter.MONTH))

    val reportState: StateFlow<ReportState> = selection
        .flatMapLatest { observeReport(it) }
        .buffer(0)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            baseState(selection.value)
        )

    fun getBillsByDate(
        timeFilter: TimeFilter,
        customStartDate: LocalDate? = null,
        customEndDate: LocalDate? = null
    ) {
        selection.update { previous ->
            if (timeFilter == TimeFilter.CUSTOM) {
                val start = customStartDate ?: previous.customStartDate ?: previous.startDate
                val end = customEndDate ?: previous.customEndDate ?: previous.endDate
                previous.copy(
                    filter = TimeFilter.CUSTOM,
                    startDate = start,
                    endDate = end,
                    timeZone = TimeZone.currentSystemDefault(),
                    customStartDate = start,
                    customEndDate = end
                )
            } else {
                presetSelection(timeFilter).copy(
                    customStartDate = previous.customStartDate,
                    customEndDate = previous.customEndDate,
                    revision = previous.revision
                )
            }
        }
    }

    fun retry() {
        selection.update { it.copy(revision = it.revision + 1) }
    }

    private fun presetSelection(filter: TimeFilter): Selection {
        val zone = TimeZone.currentSystemDefault()
        val today = Clock.System.now().toLocalDateTime(zone).date
        val days = when (filter) {
            TimeFilter.WEEK -> 7
            TimeFilter.MONTH -> 30
            TimeFilter.YEAR -> 365
            TimeFilter.CUSTOM -> error("Custom dates must be supplied separately")
        }
        return Selection(filter, today.minus(days - 1, DateTimeUnit.DAY), today, zone)
    }

    private fun baseState(selection: Selection) = ReportState(
        selectedTimeFilter = selection.filter,
        startDate = selection.startDate,
        endDate = selection.endDate,
        customStartDate = selection.customStartDate,
        customEndDate = selection.customEndDate,
        dateRangeText = "${selection.startDate.toReportDateText()} – ${selection.endDate.toReportDateText()}"
    )

    private fun observeReport(selection: Selection): Flow<ReportState> {
        val initial = baseState(selection)
        return flow {
            emit(initial)
            if (selection.startDate > selection.endDate) {
                emit(initial.copy(
                    isLoading = false,
                    error = UiText.DynamicString("Start date must not be after end date.")
                ))
                return@flow
            }
            val startInclusive = selection.startDate
                .atStartOfDayIn(selection.timeZone).toEpochMilliseconds()
            val endExclusive = selection.endDate.plus(1, DateTimeUnit.DAY)
                .atStartOfDayIn(selection.timeZone).toEpochMilliseconds()

            emitAll(getBillL(startInclusive, endExclusive).mapLatest { result ->
                when (result) {
                    is Resource.Loading -> initial
                    is Resource.Error -> initial.copy(
                        isLoading = false,
                        error = UiText.DynamicString(result.message ?: "Unknown error")
                    )
                    is Resource.Success -> withContext(Dispatchers.Default) {
                        calculateReport(result.data.orEmpty(), selection, initial)
                    }
                }
            })
        }.catch { error ->
            emit(initial.copy(
                isLoading = false,
                error = UiText.DynamicString(error.message ?: "Unknown error")
            ))
        }
    }

    private suspend fun calculateReport(
        bills: List<Bill>,
        selection: Selection,
        initial: ReportState
    ): ReportState {
        var totalAmount = 0.0
        var totalDiscount = 0.0
        var totalQuantity = 0
        val dailyAmounts = mutableMapOf<LocalDate, Double>()
        val categoryAmounts = mutableMapOf<PurchaseType, Double>()

        for (bill in bills) {
            currentCoroutineContext().ensureActive()
            var billAmount = 0.0
            for (item in bill.items) {
                currentCoroutineContext().ensureActive()
                val grossAmount = item.productPrice * item.itemCount
                val discountAmount = grossAmount * (item.discount / 100.0)
                billAmount += grossAmount - discountAmount
                totalDiscount += discountAmount
                totalQuantity += item.itemCount
            }
            totalAmount += billAmount
            val date = bill.billDate.toLocalDateTime(selection.timeZone).date
            dailyAmounts[date] = (dailyAmounts[date] ?: 0.0) + billAmount
            categoryAmounts[bill.type] = (categoryAmounts[bill.type] ?: 0.0) + billAmount
        }

        val categories = if (totalAmount > 0.0) {
            categoryAmounts.entries.filter { it.value > 0.0 }.map { (type, amount) ->
                CategoryReport(type.name, (amount / totalAmount).toFloat(), categoryColor(type))
            }.sortedByDescending { it.percentage }
        } else emptyList()

        return initial.copy(
            isLoading = false,
            totalAmount = totalAmount,
            totalDiscount = totalDiscount,
            purchaseCount = totalQuantity,
            billCount = bills.size,
            averagePerPurchase = if (bills.isEmpty()) 0.0 else totalAmount / bills.size,
            categoryReports = categories,
            barChartReport = if (bills.isEmpty()) emptyList() else buildBarChart(selection, dailyAmounts)
        )
    }

    private suspend fun buildBarChart(
        selection: Selection,
        dailyAmounts: Map<LocalDate, Double>
    ): List<BarChartReport> {
        val days = selection.endDate.toEpochDays() - selection.startDate.toEpochDays() + 1
        val monthly = selection.filter == TimeFilter.YEAR ||
            (selection.filter == TimeFilter.CUSTOM && days > 62)
        fun bucket(date: LocalDate) = if (monthly) LocalDate(date.year, date.monthNumber, 1) else date

        val amounts = mutableMapOf<LocalDate, Double>()
        for ((date, amount) in dailyAmounts) {
            currentCoroutineContext().ensureActive()
            val key = bucket(date)
            amounts[key] = (amounts[key] ?: 0.0) + amount
        }
        var cursor = bucket(selection.startDate)
        val last = bucket(selection.endDate)
        val result = mutableListOf<BarChartReport>()
        while (cursor <= last) {
            currentCoroutineContext().ensureActive()
            val month = cursor.monthNumber.toString().padStart(2, '0')
            val label = if (monthly) "$month.${cursor.year}"
                else "${cursor.dayOfMonth.toString().padStart(2, '0')}.$month"
            result += BarChartReport(label, (amounts[cursor] ?: 0.0).toFloat())
            cursor = cursor.plus(1, if (monthly) DateTimeUnit.MONTH else DateTimeUnit.DAY)
        }
        return result
    }

    private fun categoryColor(type: PurchaseType): Color = when (type) {
        PurchaseType.CLOTH -> darkYellowColor
        PurchaseType.PARTY -> redColor
        PurchaseType.FRIENDS -> orangeColor
        PurchaseType.HOUSE -> purpleColor
        PurchaseType.OTHER -> blueColor
        PurchaseType.SUPERMARKET -> darkGreenColor
    }
}
