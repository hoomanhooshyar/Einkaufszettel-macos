package com.hooman.einkaufszettel.feature.presentation.create_bill

import app.cash.turbine.test
import com.hooman.einkaufszettel.core.presentation.UiText
import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.data.local.entity.SyncStatus
import com.hooman.einkaufszettel.domain.model.Bill
import com.hooman.einkaufszettel.domain.model.PurchaseType
import com.hooman.einkaufszettel.domain.repository.AuthRepository
import com.hooman.einkaufszettel.domain.usecase.InsertBillToLocalUseCase
import com.hooman.einkaufszettel.domain.usecase.SyncDatabaseUseCase
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock
import org.junit.After
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class CreateBillViewModelTest{

    //Dependencies simulations
    private lateinit var insertBill: InsertBillToLocalUseCase
    private lateinit var authRepository: AuthRepository
    private lateinit var syncDatabaseUseCase: SyncDatabaseUseCase

    //The class, that we want to test
    private lateinit var viewModel: CreateBillViewModel

    //This Dispatcher is for suspend functions test
    private val testDispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup(){
        //Set Main Dispatcher for ViewModel
        Dispatchers.setMain(testDispatcher)

        insertBill = mockk()
        authRepository = mockk()
        syncDatabaseUseCase = mockk()

        viewModel = CreateBillViewModel(
            insertBillL = insertBill,
            authRepository = authRepository,
            syncDatabaseUseCase = syncDatabaseUseCase
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown(){
        Dispatchers.resetMain()
    }

    /*
     *Test that what happen when "Bill" is null
     */
    @Test
    fun `addBillIntoLocal with null bill emits error state`() = runTest {

        viewModel.createListState.test {
            //Fetch "Init Situation"
            val initialState = awaitItem()
            assertFalse(initialState.isLoading)


            viewModel.addBillIntoLocal(null)

            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            val finalState = awaitItem()
            assertFalse(finalState.isLoading)
            assertTrue(finalState.error != null)

            cancelAndIgnoreRemainingEvents()
        }
    }

    /**
     * Save Data into Local successfully and Status changing
     */
    @Test
    fun `successful save triggers LSL status and background sync`() = runTest {

        //**************Given (Initialize a fake Bill)************
        val testUserId = "user_123"


        val initialBill = Bill(
            id = "1",
            billDate = Clock.System.now(),
            name = "Test Bill 123",
            type = PurchaseType.SUPERMARKET,
            userId = "",
            items = emptyList(),
            syncStatus = SyncStatus.SUCCESS
        )

        //Mock's response to function call
        coEvery{authRepository.getCurrentUserId()} returns testUserId
        coEvery { insertBill(any()) } returns Resource.Success(Unit)
        coEvery { syncDatabaseUseCase() } just Runs


        val captureBill = slot<Bill>()
        coEvery { insertBill(capture(captureBill)) } returns Resource.Success(Unit)


        //**************When and Then************

        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.createListState.test {
            awaitItem()

            viewModel.addBillIntoLocal(initialBill)
            assertTrue(awaitItem().isLoading)

            val finalState = awaitItem()
            assertFalse(finalState.isLoading)

            cancelAndIgnoreRemainingEvents()
        }

        assertEquals(testUserId, captureBill.captured.userId)

        assertEquals(SyncStatus.LSL, captureBill.captured.syncStatus)

        coVerify(exactly = 1) { syncDatabaseUseCase() }
    }

    /**
     * Test when unregister user wants to save a Bill
     * Returns Error
     */
    @Test
    fun `Unloggedin user wants to save a Bill_retrun error`() = runTest {


        val initialBill = Bill(
            id = "1",
            billDate = Clock.System.now(),
            name = "Test Bill 123",
            type = PurchaseType.SUPERMARKET,
            userId = "",
            items = emptyList(),
            syncStatus = SyncStatus.LSL
        )

        //
        coEvery { authRepository.getCurrentUserId() } returns null
        viewModel.createListState.test {
            awaitItem()
            viewModel.addBillIntoLocal(initialBill)

            assertTrue(awaitItem().isLoading)

            val finalState = awaitItem()
            assertFalse(finalState.isLoading)
            assertTrue(finalState.error != null)

            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 0) { insertBill(any()) }
        coVerify(exactly = 0) { syncDatabaseUseCase() }


    }

    /**
     * Test insert into local fail because of an error
     * Returns Error
     */
    @Test
    fun `Fail to insert into Local because of an error`() = runTest {
        val testUserId = "user_123"
        val databaseErrorMessage = "Disk is full or Database locked"

        val initialBill = Bill(
            id = "1",
            billDate = Clock.System.now(),
            name = "Test Bill 123",
            type = PurchaseType.SUPERMARKET,
            userId = "",
            items = emptyList(),
            syncStatus = SyncStatus.LSL
        )

        coEvery { authRepository.getCurrentUserId() } returns testUserId

        coEvery { insertBill(any()) } returns Resource.Error(databaseErrorMessage)

        viewModel.createListState.test {
            awaitItem()

            viewModel.addBillIntoLocal(initialBill)

            assertTrue(awaitItem().isLoading)

            val finalState = awaitItem()
            assertFalse(finalState.isLoading)
            assertTrue(finalState.error != null)

            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 1) { insertBill(any()) }
        coVerify(exactly = 0) { syncDatabaseUseCase() }
    }

    /**
     * Test SyncDatabase has an error by the way the app doesn't crash
     */
    @Test
    fun `background sync exception does not crash the app and local save succeeds`() = runTest {
        val testUserId = "user123"
        val initialBill = Bill(
            id = "1",
            billDate = Clock.System.now(),
            name = "Test Bill 123",
            type = PurchaseType.SUPERMARKET,
            userId = "",
            items = emptyList(),
            syncStatus = SyncStatus.LSL
        )

        coEvery { authRepository.getCurrentUserId() } returns testUserId
        coEvery { insertBill(any()) } returns Resource.Success(Unit)

        coEvery { syncDatabaseUseCase() } throws RuntimeException("Server is completely down!")

        viewModel.createListState.test {
            awaitItem()

            viewModel.addBillIntoLocal(initialBill)
            assertTrue(awaitItem().isLoading)

            val finalState = awaitItem()
            assertFalse(finalState.isLoading)
            assertTrue(finalState.error != null)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Test if Flow has an error but error variable is null, it shows Unknown Error`() = runTest {
        val userId = "user123"
        val initialBill = Bill(
            id = "1",
            billDate = Clock.System.now(),
            name = "Test Bill 123",
            type = PurchaseType.SUPERMARKET,
            userId = "",
            items = emptyList(),
            syncStatus = SyncStatus.LSL
        )

        coEvery { authRepository.getCurrentUserId() } returns userId
        coEvery { insertBill(any()) } returns Resource.Error(null)

        viewModel.createListState.test {
            awaitItem()

            viewModel.addBillIntoLocal(initialBill)
            assertTrue(awaitItem().isLoading)

            val finalState = awaitItem()

            assertFalse(finalState.isLoading)
            assertTrue(finalState.error != null)
            assertEquals(UiText.DynamicString("Unknown Error"), finalState.error)

            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 0) { syncDatabaseUseCase() }
    }

    /**
     * Test Handle AuthRepository Error
     */
    @Test
    fun `Test handle AuthRepository Error`() = runTest {
        val userId = "user123"
        val initialBill = Bill(
            id = "1",
            billDate = Clock.System.now(),
            name = "Test Bill 123",
            type = PurchaseType.SUPERMARKET,
            userId = "",
            items = emptyList(),
            syncStatus = SyncStatus.LSL
        )
        coEvery { authRepository.getCurrentUserId() } throws RuntimeException("Error")
        viewModel.createListState.test {
            awaitItem()

            viewModel.addBillIntoLocal(initialBill)

            assertTrue(awaitItem().isLoading)
            val finalState = awaitItem()
            assertFalse(finalState.isLoading)
            assertTrue(finalState.error != null)
            assertEquals(UiText.DynamicString("Unknown Error"), finalState.error)
        }

        coVerify(exactly = 0) { syncDatabaseUseCase() }
    }
}