package com.hooman.einkaufszettel.data.repository

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.data.local.dao.FakeAppDao
import com.hooman.einkaufszettel.data.local.entity.ProductEntity
import com.hooman.einkaufszettel.data.local.entity.SyncStatus
import com.hooman.einkaufszettel.domain.model.Product
import com.hooman.einkaufszettel.domain.repository.AuthRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class LocalRepositoryIsolationTest {
    private val account = MutableStateFlow<String?>("A")
    private val auth = mockk<AuthRepository> {
        every { userId } returns account
        every { getCurrentUserId() } answers { account.value }
    }
    private val dao = FakeAppDao()
    private val repository = LocalRepositoryImpl(dao, auth)

    private fun entity(id: String, owner: String) =
        ProductEntity(id, id, null, 1.0, owner, SyncStatus.LSL)

    @Test
    fun `existing collector follows account changes and clears on logout`() = runTest {
        dao.insertProduct(entity("a", "A"))
        dao.insertProduct(entity("b", "B"))
        var ids = emptyList<String>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            repository.getAllProducts().collect { result ->
                if (result is Resource.Success) ids = result.data.orEmpty().map { it.id }
            }
        }
        runCurrent()
        assertEquals(listOf("a"), ids)
        account.value = "B"
        runCurrent()
        assertEquals(listOf("b"), ids)
        account.value = null
        runCurrent()
        assertEquals(emptyList(), ids)
    }

    @Test
    fun `pending uploads only contain the active account`() = runTest {
        dao.insertProduct(entity("a", "A"))
        dao.insertProduct(entity("b", "B"))
        val result = repository.getProductUnSyncData().first { it is Resource.Success }
        assertEquals(listOf("a"), result.data.orEmpty().map { it.id })
    }

    @Test
    fun `writes reject foreign ownership and unauthenticated users`() = runTest {
        val foreign = Product(id = "b", name = "B", image = null, price = 1.0,
            userId = "B", syncStatus = SyncStatus.LSL)
        assertIs<Resource.Error<Unit>>(repository.insertProduct(foreign))
        account.value = null
        assertIs<Resource.Error<Unit>>(repository.insertProduct(foreign.copy(userId = "")))
    }

    @Test
    fun `deleting another accounts product leaves it intact`() = runTest {
        dao.insertProduct(entity("b", "B"))
        val foreign = Product(id = "b", name = "B", image = null, price = 1.0,
            userId = "B", syncStatus = SyncStatus.LSL)
        assertIs<Resource.Error<Unit>>(repository.deleteProduct(foreign))
        assertEquals(listOf("b"), dao.getAllProducts("B").first().map { it.id })
    }
}
