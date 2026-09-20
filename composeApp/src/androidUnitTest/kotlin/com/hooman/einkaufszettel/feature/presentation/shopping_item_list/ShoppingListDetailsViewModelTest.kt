package com.hooman.einkaufszettel.feature.presentation.shopping_item_list

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.hooman.einkaufszettel.core.network.ConnectivityObserver
import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.data.local.entity.SyncStatus
import com.hooman.einkaufszettel.domain.model.ShoppingDetails
import com.hooman.einkaufszettel.domain.repository.AuthRepository
import com.hooman.einkaufszettel.domain.repository.LocalRepository
import com.hooman.einkaufszettel.domain.repository.FirebaseShoppingItemRepository
import com.hooman.einkaufszettel.domain.usecase.*
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class ShoppingListDetailsViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val local = mockk<LocalRepository>()
    private val remote = mockk<FirebaseShoppingItemRepository>()
    private val sync = mockk<SyncDatabaseUseCase>()
    private lateinit var viewModel: ShoppingListDetailsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        // Route decoding uses Bundle; isolate Android navigation from these JVM behavior tests.
        mockkConstructor(Bundle::class)
        every { anyConstructed<Bundle>().putCharSequence(any(), any()) } just Runs
        every { anyConstructed<Bundle>().get("billId") } returns "bill"
        val item = ShoppingDetails("product", "", "Product", 20.0, "item",
            5f, false, 1, "bill", "user", SyncStatus.SUCCESS)
        every { local.getBillById("bill") } returns flowOf(Resource.Loading())
        every { local.getProductsForShoppingItem("bill") } returns flowOf(Resource.Success(listOf(item)))
        coEvery { local.updateSHoppingItemCount(any(), any()) } returns Resource.Success(Unit)
        coEvery { local.updateShoppingItemDiscount(any(), any()) } returns Resource.Success(Unit)
        coEvery { remote.updateShoppingItemCount(any(), any(), any()) } returns Resource.Success(Unit)
        coEvery { remote.updateShoppingItemDiscount(any(), any(), any()) } returns Resource.Success(Unit)
        coEvery { sync() } returns Unit
        val observer = mockk<ConnectivityObserver> {
            every { isConnected } returns flowOf(true)
        }
        val auth = mockk<AuthRepository> {
            every { getCurrentUserId() } returns "user"
        }
        viewModel = ShoppingListDetailsViewModel(
            SavedStateHandle(mapOf("billId" to "bill")),
            GetBillByIdFromLocalUseCase(local),
            GetShoppingItemByBillIdFromRemoteUseCase(remote),
            InsertShoppingItemToLocalUseCase(local),
            GetProductForShoppingItemFromLocalUseCase(local),
            DeleteShoppingItemFromLocalUseCase(local),
            DeleteShoppingItemFromRemoteUseCase(remote),
            UpdateShoppingItemCountInLocalUseCase(local),
            UpdateShoppingItemCheckStatusInLocalUseCase(local),
            UpdateShoppingItemDiscountInLocalUseCase(local),
            sync, observer, auth
        )
    }

    @After
    fun teardown() {
        if (::viewModel.isInitialized) viewModel.viewModelScope.cancel()
        unmockkConstructor(Bundle::class)
        Dispatchers.resetMain()
    }

    @Test
    fun `quantity then discount both persist without changing base price`() = runTest(dispatcher) {
        runCurrent()
        viewModel.updateShoppingItemCount("item", 3)
        viewModel.updateDiscount("item", 25f)
        val item = viewModel.listDetailsState.value.shoppingDetailsItems!!.single()
        assertEquals(20.0, item.productPrice)
        assertEquals(25f, item.discount)
        assertEquals(45.0, viewModel.getTotalAmount())
        advanceUntilIdle()
        coVerify(exactly = 1) { local.updateSHoppingItemCount("item", 3) }
        coVerify(exactly = 1) { local.updateShoppingItemDiscount("item", 25f) }
        coVerify(exactly = 2) { sync() }
    }

    @Test
    fun `repeated edits debounce independently in reverse order`() = runTest(dispatcher) {
        runCurrent()
        viewModel.updateDiscount("item", 10f)
        viewModel.updateShoppingItemCount("item", 2)
        runCurrent()
        advanceTimeBy(200)
        viewModel.updateDiscount("item", 30f)
        viewModel.updateShoppingItemCount("item", 4)
        advanceUntilIdle()
        coVerify(exactly = 0) { local.updateShoppingItemDiscount("item", 10f) }
        coVerify(exactly = 0) { local.updateSHoppingItemCount("item", 2) }
        coVerify(exactly = 1) { local.updateShoppingItemDiscount("item", 30f) }
        coVerify(exactly = 1) { local.updateSHoppingItemCount("item", 4) }
    }

    @Test
    fun `invalid discounts do not change state or persist`() = runTest(dispatcher) {
        runCurrent()
        listOf(-1f, 0f, 100f, 101f, Float.NaN, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY)
            .forEach { viewModel.updateDiscount("item", it) }
        advanceUntilIdle()
        assertEquals(5f, viewModel.listDetailsState.value.shoppingDetailsItems!!.single().discount)
        assertNotNull(viewModel.listDetailsState.value.error)
        coVerify(exactly = 0) { local.updateShoppingItemDiscount(any(), any()) }
        coVerify(exactly = 0) { remote.updateShoppingItemDiscount(any(), any(), any()) }
    }
}
