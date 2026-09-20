package com.hooman.einkaufszettel.data.repository

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.data.local.dao.FakeAppDao
import com.hooman.einkaufszettel.data.local.entity.*
import com.hooman.einkaufszettel.data.mapper.toProduct
import com.hooman.einkaufszettel.domain.repository.*
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.*

class SyncSafetyTest {
    private val dao = FakeAppDao()
    private val auth = mockk<AuthRepository> {
        every { getCurrentUserId() } returns "user"
        every { userId } returns flowOf("user")
    }
    private val local = LocalRepositoryImpl(dao, auth)
    private val products = mockk<FirebaseProductRepository>()
    private val bills = mockk<FirebaseBillRepository>()
    private val items = mockk<FirebaseShoppingItemRepository>()
    private val sync = SyncRepositoryImpl(local, products, bills, items, auth)
    private val product = ProductEntity("p", "Original", null, 10.0, "user", SyncStatus.LSL)
    private val bill = BillEntity("b", Instant.fromEpochMilliseconds(1), "Bill", "SUPERMARKET", "user", SyncStatus.LSL)
    private val item = ShoppingItemEntity("i", "b", "p", 1, 0f, false, "user", SyncStatus.LSL)

    init {
        every { products.getAllProductsByUserId("user") } returns flowOf(Resource.Success(emptyList()))
        every { bills.getAllBillsByUserId("user") } returns flowOf(Resource.Success(emptyList()))
        every { items.getAllShoppingItemsByUserId("user") } returns flowOf(Resource.Success(emptyList()))
    }

    @Test
    fun `concurrent sync calls execute serially`() = runTest {
        val active = AtomicInteger()
        val peak = AtomicInteger()
        every { products.getAllProductsByUserId("user") } returns flow {
            val count = active.incrementAndGet()
            peak.updateAndGet { maxOf(it, count) }
            try {
                delay(30)
                emit(Resource.Success(emptyList()))
            } finally {
                active.decrementAndGet()
            }
        }
        coroutineScope {
            launch { sync.syncDatabase() }
            launch { sync.syncDatabase() }
        }
        assertEquals(1, peak.get())
        verify(exactly = 2) { products.getAllProductsByUserId("user") }
    }

    @Test
    fun `edit during upload stays dirty and wins over pull`() = runTest {
        dao.insertProduct(product)
        coEvery { products.insertProduct(any()) } coAnswers {
            dao.insertProduct(product.copy(name = "Newer local edit"))
            Resource.Success(Unit)
        }
        every { products.getAllProductsByUserId("user") } returns flowOf(
            Resource.Success(listOf(product.copy(syncStatus = SyncStatus.SUCCESS).toProduct()))
        )
        sync.syncDatabase()
        val current = dao.readProduct("p", "user")!!
        assertEquals("Newer local edit", current.name)
        assertEquals(SyncStatus.LSL, current.syncStatus)
    }

    @Test
    fun `failed upload remains dirty and cannot be overwritten by pull`() = runTest {
        dao.insertProduct(product)
        coEvery { products.insertProduct(any()) } returns Resource.Error("Offline")
        every { products.getAllProductsByUserId("user") } returns flowOf(
            Resource.Success(listOf(product.copy(name = "Old remote", syncStatus = SyncStatus.SUCCESS).toProduct()))
        )
        sync.syncDatabase()
        assertEquals("Original", dao.readProduct("p", "user")!!.name)
        assertEquals(SyncStatus.RSF, dao.readProduct("p", "user")!!.syncStatus)
    }

    @Test
    fun `remote single and batch imports preserve dirty rows of every type`() = runTest {
        dao.insertProduct(product)
        dao.insertBill(bill)
        dao.insertShoppingItem(item)
        dao.insertProduct(product.copy(name = "Remote", syncStatus = SyncStatus.SUCCESS))
        dao.insertBillList(listOf(bill.copy(name = "Remote", syncStatus = SyncStatus.SUCCESS)))
        dao.insertItemList(listOf(item.copy(itemCount = 9, syncStatus = SyncStatus.SUCCESS)))
        assertEquals(product, dao.readProduct("p", "user"))
        assertEquals(bill, dao.readBill("b", "user"))
        assertEquals(item, dao.readShoppingItem("i", "user"))
    }

    @Test
    fun `acknowledgements reject changed or deleted snapshots and accept unchanged ones`() = runTest {
        dao.insertProduct(product)
        dao.insertBill(bill)
        dao.insertShoppingItem(item)
        dao.insertBill(bill.copy(name = "Edited"))
        dao.updateShoppingItemDiscount("i", 25f, "user")
        assertFalse(dao.acknowledgeBill(bill, SyncStatus.SUCCESS))
        assertFalse(dao.acknowledgeShoppingItem(item, SyncStatus.SUCCESS))
        assertTrue(dao.acknowledgeProduct(product, SyncStatus.SUCCESS))
        dao.deleteShoppingItem("i", "user")
        assertFalse(dao.acknowledgeShoppingItem(item, SyncStatus.SUCCESS))
        assertNull(dao.readShoppingItem("i", "user"))
    }

    @Test
    fun `quantity checked and discount edits mark clean rows dirty`() = runTest {
        dao.insertProduct(product)
        dao.insertBill(bill)
        dao.insertShoppingItem(item.copy(syncStatus = SyncStatus.SUCCESS))
        dao.updateShoppingItemCount("i", 2, "user")
        assertEquals(SyncStatus.LSL, dao.readShoppingItem("i", "user")!!.syncStatus)
        dao.acknowledgeShoppingItem(dao.readShoppingItem("i", "user")!!, SyncStatus.SUCCESS)
        dao.updateShoppingItemCheckStatus("i", true, "user")
        assertEquals(SyncStatus.LSL, dao.readShoppingItem("i", "user")!!.syncStatus)
        dao.acknowledgeShoppingItem(dao.readShoppingItem("i", "user")!!, SyncStatus.SUCCESS)
        dao.updateShoppingItemDiscount("i", 25f, "user")
        assertEquals(SyncStatus.LSL, dao.readShoppingItem("i", "user")!!.syncStatus)
    }

    @Test
    fun `cancelled sync releases the mutex for the next call`() = runTest {
        val entered = CompletableDeferred<Unit>()
        every { products.getAllProductsByUserId("user") } returns flow {
            entered.complete(Unit)
            awaitCancellation()
        }
        val job = launch { sync.syncDatabase() }
        entered.await()
        job.cancelAndJoin()
        every { products.getAllProductsByUserId("user") } returns flowOf(Resource.Success(emptyList()))
        // Use a real-time timeout because the repository dispatches to Dispatchers.IO.
        withContext(Dispatchers.Default) {
            withTimeout(2000) { sync.syncDatabase() }
        }
    }
}
