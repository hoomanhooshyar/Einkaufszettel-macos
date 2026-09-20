package com.hooman.einkaufszettel.data.repository

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.data.remote.FirebaseBillDataSourceImpl
import com.hooman.einkaufszettel.domain.model.Bill
import com.hooman.einkaufszettel.domain.source.FirebaseService
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class FirebaseBillWriteTest {
    private val service = mockk<FirebaseService>()
    private val bill = mockk<Bill>(relaxed = true)
    private val dataSource = FirebaseBillDataSourceImpl(service)
    private val repository = FirebaseBillRepositoryImpl(dataSource)

    @Test
    fun `failure inside write block propagates through datasource to repository error`() = runTest {
        coEvery { service.io<Unit>(any()) } coAnswers {
            firstArg<suspend () -> Unit>().invoke()
        }
        every { service.billsCol() } throws IllegalStateException("Firestore unavailable")
        assertFailsWith<IllegalStateException> { dataSource.insertBill(bill) }
        val result = repository.insertBill(bill)
        assertIs<Resource.Error<Unit>>(result)
        assertEquals("Firestore unavailable", result.message)
    }

    @Test
    fun `cancellation is propagated rather than converted to a save error`() = runTest {
        coEvery { service.io<Unit>(any()) } throws CancellationException("Cancelled")
        assertFailsWith<CancellationException> { repository.insertBill(bill) }
    }
}
