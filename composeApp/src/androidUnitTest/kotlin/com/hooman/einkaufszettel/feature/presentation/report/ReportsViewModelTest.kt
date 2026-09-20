package com.hooman.einkaufszettel.feature.presentation.report

import androidx.lifecycle.viewModelScope
import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.data.local.entity.SyncStatus
import com.hooman.einkaufszettel.domain.model.Bill
import com.hooman.einkaufszettel.domain.model.PurchaseType
import com.hooman.einkaufszettel.domain.model.ShoppingItem
import com.hooman.einkaufszettel.domain.repository.LocalRepository
import com.hooman.einkaufszettel.domain.usecase.GetBillsByDateFromLocalUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.TimeZone
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class ReportsViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val repository = mockk<LocalRepository>()
    private lateinit var viewModel: ReportsViewModel
    private lateinit var originalZone: TimeZone

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        originalZone = TimeZone.getDefault()
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Vienna"))
    }

    @After
    fun tearDown() {
        if (::viewModel.isInitialized) viewModel.viewModelScope.cancel()
        TimeZone.setDefault(originalZone)
        Dispatchers.resetMain()
    }

    private fun createViewModel() {
        viewModel = ReportsViewModel(GetBillsByDateFromLocalUseCase(repository))
    }

    private fun bill(date: Instant = Clock.System.now()) = Bill(
        id = "bill", billDate = date, name = "Purchase", userId = "user",
        type = PurchaseType.SUPERMARKET, syncStatus = SyncStatus.SUCCESS,
        items = listOf(ShoppingItem(
            id = "item", billId = "bill", productId = "product",
            itemCount = 3, discount = 20f, productName = "Product",
            productPrice = 10.0, productImage = null, isChecked = true,
            userId = "user", syncStatus = SyncStatus.SUCCESS
        ))
    )

    @Test
    fun `month covers 30 dates and all outputs use quantity and net spending`() = runTest(dispatcher) {
        every { repository.getBillByDate(any(), any()) } returns flowOf(Resource.Success(listOf(bill())))
        createViewModel()
        val state = viewModel.reportState.first { !it.isLoading }
        assertNull(state.error)
        assertEquals(24.0, state.totalAmount)
        assertEquals(6.0, state.totalDiscount)
        assertEquals(3, state.purchaseCount)
        assertEquals(24.0, state.averagePerPurchase)
        assertEquals(30, state.barChartReport.size)
        assertEquals(24.0, state.barChartReport.sumOf { it.value.toDouble() })
        assertEquals(1f, state.categoryReports.single().percentage)
    }

    @Test
    fun `custom same day uses Vienna DST boundaries and produces a bar`() = runTest(dispatcher) {
        var start = 0L
        var end = 0L
        every { repository.getBillByDate(any(), any()) } answers {
            start = firstArg()
            end = secondArg()
            flowOf(Resource.Success(listOf(bill(Instant.parse("2026-03-29T12:00:00Z")))))
        }
        createViewModel()
        viewModel.getBillsByDate(TimeFilter.CUSTOM, LocalDate(2026, 3, 29), LocalDate(2026, 3, 29))
        val state = viewModel.reportState.first { !it.isLoading && it.selectedTimeFilter == TimeFilter.CUSTOM }
        assertEquals(Instant.parse("2026-03-28T23:00:00Z").toEpochMilliseconds(), start)
        assertEquals(Instant.parse("2026-03-29T22:00:00Z").toEpochMilliseconds(), end)
        assertEquals(23 * 60 * 60 * 1000L, end - start)
        assertEquals(24f, state.barChartReport.single().value)
    }

    @Test
    fun `changing filter cancels old query before starting another`() = runTest(dispatcher) {
        var active = 0
        var maximum = 0
        var cancelled = 0
        every { repository.getBillByDate(any(), any()) } answers {
            flow {
                active++
                maximum = maxOf(maximum, active)
                try {
                    emit(Resource.Success(listOf(bill())))
                    awaitCancellation()
                } finally {
                    active--
                    cancelled++
                }
            }
        }
        createViewModel()
        viewModel.reportState.first { !it.isLoading }
        viewModel.getBillsByDate(TimeFilter.WEEK)
        viewModel.reportState.first { !it.isLoading && it.selectedTimeFilter == TimeFilter.WEEK }
        viewModel.getBillsByDate(TimeFilter.CUSTOM, LocalDate(2026, 1, 1), LocalDate(2026, 1, 2))
        viewModel.reportState.first { !it.isLoading && it.selectedTimeFilter == TimeFilter.CUSTOM }
        assertEquals(1, active)
        assertEquals(1, maximum)
        assertEquals(2, cancelled)
    }
}
