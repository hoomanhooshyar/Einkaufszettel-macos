package com.hooman.einkaufszettel.feature.presentation.report

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hooman.einkaufszettel.core.network.ConnectivityObserver
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
import com.hooman.einkaufszettel.domain.repository.AuthRepository
import com.hooman.einkaufszettel.domain.usecase.GetBillsByDateFromLocalUseCase
import einkaufszettel.composeapp.generated.resources.Res
import einkaufszettel.composeapp.generated.resources.apr
import einkaufszettel.composeapp.generated.resources.aug
import einkaufszettel.composeapp.generated.resources.dec
import einkaufszettel.composeapp.generated.resources.feb
import einkaufszettel.composeapp.generated.resources.friday
import einkaufszettel.composeapp.generated.resources.jan
import einkaufszettel.composeapp.generated.resources.jul
import einkaufszettel.composeapp.generated.resources.jun
import einkaufszettel.composeapp.generated.resources.mar
import einkaufszettel.composeapp.generated.resources.may
import einkaufszettel.composeapp.generated.resources.monday
import einkaufszettel.composeapp.generated.resources.nov
import einkaufszettel.composeapp.generated.resources.oct
import einkaufszettel.composeapp.generated.resources.saturday
import einkaufszettel.composeapp.generated.resources.sep
import einkaufszettel.composeapp.generated.resources.sunday
import einkaufszettel.composeapp.generated.resources.thursday
import einkaufszettel.composeapp.generated.resources.tuesday
import einkaufszettel.composeapp.generated.resources.wednesday
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlin.collections.emptyList

import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime


class ReportsViewModel(
    private val getBillL: GetBillsByDateFromLocalUseCase,
    private val observer: ConnectivityObserver,
    private val auth: AuthRepository
) : ViewModel() {
    private val _reportState = MutableStateFlow(ReportState())
    val reportState: StateFlow<ReportState> = _reportState.asStateFlow()

    private val _userId = MutableStateFlow<String?>(null)
    val userId = _userId.asStateFlow()

    init {
        _userId.value = auth.getCurrentUserId()
    }

    @OptIn(ExperimentalTime::class)
    fun getBillsByDate(
        timeFilter: TimeFilter,
        customStartDate: Instant?,
        customEndDate: Instant?
    ) {
        val now = Clock.System.now()
        var endDate: Instant = now
        var startDate: Instant = now
        when (timeFilter) {
            TimeFilter.WEEK -> startDate -= 7.days
            TimeFilter.MONTH -> startDate -= 30.days
            TimeFilter.YEAR -> startDate -= 365.days
            TimeFilter.CUSTOM -> {
                startDate = customStartDate ?: (now - 7.days)
                endDate = customEndDate ?: now
            }
        }
        viewModelScope.launch {

            getBillL(
                startDate = startDate.toEpochMilliseconds(),
                endDate = endDate.toEpochMilliseconds()
            ).collect { res ->
                when (res) {
                    is Resource.Success -> {
                        val totalAmount = calculateTotalAmount(res.data ?: emptyList())
                        val totalDiscount = calculateDiscount(res.data ?: emptyList())
                        val average = calculateAveragePerPurchase(res.data ?: emptyList())
                        val totalItem = calculateTotalItems(res.data ?: emptyList())
                        val categoryReports = getDonutChartData(res.data ?: emptyList())
                        val barChartReport = barChartData(res.data ?: emptyList(), timeFilter)
                        _reportState.value = _reportState.value.copy(
                            isLoading = false,
                            error = null,
                            totalAmount = totalAmount,
                            averagePerPurchase = average,
                            purchaseCount = totalItem,
                            selectedTimeFilter = timeFilter,
                            categoryReports = categoryReports,
                            barChartReport = barChartReport,
                            totalDiscount = totalDiscount
                            )
                    }

                    is Resource.Loading -> {
                        _reportState.value = _reportState.value.copy(
                            isLoading = true,
                            error = null
                        )
                    }

                    is Resource.Error -> {
                        _reportState.value = _reportState.value.copy(
                            isLoading = false,
                            error = UiText.DynamicString(res.message ?: "Unknown error")
                        )
                    }
                }
            }

        }
    }

    private fun calculateTotalAmount(bills: List<Bill>): Double{
        return bills.sumOf { bill ->
            bill.items.sumOf { item ->
                item.itemCount * item.productPrice
            }
        }
    }

    private fun calculateDiscount(bills: List<Bill>): Double{
        return bills.sumOf { bill ->
            bill.items.sumOf { item ->
                (item.productPrice * item.itemCount) * (item.discount / 100)
            }
        }
    }

    private fun calculateTotalItems(bills: List<Bill>): Int{
        return bills.sumOf { bill ->
            bill.items.size
        }
    }

    private fun calculateAveragePerPurchase(bills: List<Bill>): Double{
        val totalAmount = calculateTotalAmount(bills)
        val totalBills = calculateTotalBills(bills)
        return if(totalBills > 0) totalAmount / totalBills else 0.0
    }

    private fun calculateTotalBills(bills: List<Bill>): Int{
        return bills.size
    }

    private fun getDonutChartData(bills: List<Bill>): List<CategoryReport> {

        val totalPrice = calculateTotalAmount(bills)

        if (totalPrice == 0.0) return emptyList()

        val groupedBills: Map<PurchaseType, List<Bill>> = bills.groupBy { it.type }

        val categoryReports = groupedBills.map { (type, billsOfType) ->
            val chartColor: Color = when(type){
                PurchaseType.CLOTH -> darkYellowColor
                PurchaseType.PARTY -> redColor
                PurchaseType.FRIENDS -> orangeColor
                PurchaseType.HOUSE -> purpleColor
                PurchaseType.OTHER -> blueColor
                PurchaseType.SUPERMARKET -> darkGreenColor
            }

            val catPrice = billsOfType.sumOf { bill ->
                bill.items.sumOf { item ->
                    item.itemCount * item.productPrice
                }
            }
            val percentage = (catPrice / totalPrice).toFloat()
            CategoryReport(
                categoryName = type.name,
                percentage = percentage,
                color = chartColor
            )
        }

        return categoryReports.sortedByDescending { it.percentage }

    }

    @OptIn(ExperimentalTime::class)
    private suspend fun barChartData(bills: List<Bill>, timeFilter: TimeFilter): List<BarChartReport>{
        if(bills.isEmpty()) return emptyList()
        val timeZone = TimeZone.currentSystemDefault()
        val barColor = redColor

        return when(timeFilter){
            TimeFilter.WEEK -> {
                val daysOfWeek = listOf(
                    DayOfWeek.MONDAY to UiText.StringResourceId(Res.string.monday).resolve(),
                    DayOfWeek.TUESDAY to UiText.StringResourceId(Res.string.tuesday).resolve(),
                    DayOfWeek.WEDNESDAY to UiText.StringResourceId(Res.string.wednesday).resolve(),
                    DayOfWeek.THURSDAY to UiText.StringResourceId(Res.string.thursday).resolve(),
                    DayOfWeek.FRIDAY to UiText.StringResourceId(Res.string.friday).resolve(),
                    DayOfWeek.SATURDAY to UiText.StringResourceId(Res.string.saturday).resolve(),
                    DayOfWeek.SUNDAY to UiText.StringResourceId(Res.string.sunday).resolve(),
                )

                daysOfWeek.map { (dayEnum, label) ->
                    val dayBills = bills.filter { bill ->
                        bill.billDate.toLocalDateTime(timeZone).dayOfWeek == dayEnum
                    }
                    val totalAmount = dayBills.sumOf { bill ->
                        bill.items.sumOf { it.productPrice * it.itemCount }
                    }

                    BarChartReport(
                        label = label,
                        value = totalAmount.toFloat(),
                    )
                }
            }
            TimeFilter.MONTH -> {
                val now = Clock.System.now()
                val today = now.toLocalDateTime(timeZone).date
                val last30Days = (20 downTo 0).map { dayAgo ->
                    today.minus(dayAgo, DateTimeUnit.DAY)
                }

                last30Days.map { targetDate ->
                    val dayBills = bills.filter { bill ->
                        bill.billDate.toLocalDateTime(timeZone).date == targetDate
                    }

                    val totalAmount = dayBills.sumOf { bill ->
                        bill.items.sumOf { it.productPrice * it.itemCount }
                    }

                    val dayStr = targetDate.dayOfMonth.toString().padStart(2,'0')
                    val monStr = targetDate.monthNumber.toString().padStart(2, '0')
                    val label = "$dayStr.$monStr"

                    BarChartReport(
                        label = label,
                        value = totalAmount.toFloat()
                    )
                }
            }
            TimeFilter.YEAR -> {
                val monthsOfYear = listOf(
                    Month.JANUARY to UiText.StringResourceId(Res.string.jan).resolve(),
                    Month.FEBRUARY to UiText.StringResourceId(Res.string.feb).resolve(),
                    Month.MARCH to UiText.StringResourceId(Res.string.mar).resolve(),
                    Month.APRIL to UiText.StringResourceId(Res.string.apr).resolve(),
                    Month.MAY to UiText.StringResourceId(Res.string.may).resolve(),
                    Month.JUNE to UiText.StringResourceId(Res.string.jun).resolve(),
                    Month.JULY to UiText.StringResourceId(Res.string.jul).resolve(),
                    Month.AUGUST to UiText.StringResourceId(Res.string.aug).resolve(),
                    Month.SEPTEMBER to UiText.StringResourceId(Res.string.sep).resolve(),
                    Month.OCTOBER to UiText.StringResourceId(Res.string.oct).resolve(),
                    Month.NOVEMBER to UiText.StringResourceId(Res.string.nov).resolve(),
                    Month.DECEMBER to UiText.StringResourceId(Res.string.dec).resolve()
                )
                monthsOfYear.map { (monthEnum, label) ->
                    val monthBills = bills.filter { bill ->
                        bill.billDate.toLocalDateTime(timeZone).month == monthEnum
                    }
                    val totalAmount = monthBills.sumOf { bill ->
                        bill.items.sumOf { it.productPrice * it.itemCount }
                    }

                    BarChartReport(
                        label = label,
                        value = totalAmount.toFloat()
                    )
                }
            }
            TimeFilter.CUSTOM -> {
                emptyList()
            }
        }

    }
}