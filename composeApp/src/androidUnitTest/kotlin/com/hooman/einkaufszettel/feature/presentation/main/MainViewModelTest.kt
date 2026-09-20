package com.hooman.einkaufszettel.feature.presentation.main

import androidx.lifecycle.viewModelScope
import com.hooman.einkaufszettel.app.Routes
import com.hooman.einkaufszettel.core.network.ConnectivityObserver
import com.hooman.einkaufszettel.domain.repository.AuthRepository
import com.hooman.einkaufszettel.domain.repository.SettingsPreferences
import com.hooman.einkaufszettel.domain.usecase.SyncDatabaseUseCase
import com.hooman.einkaufszettel.feature.presentation.start.StartViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val observer = mockk<ConnectivityObserver>()
    private val auth = mockk<AuthRepository>()
    private val settingsPreferences = mockk<SettingsPreferences>()
    private val sync = mockk<SyncDatabaseUseCase>()
    private var viewModel: MainViewModel? = null

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        every { auth.getCurrentUserId() } returns "user"
        every { settingsPreferences.languageFlow } returns MutableStateFlow("en")
        coEvery { sync() } returns Unit
    }

    @After
    fun tearDown() {
        viewModel?.viewModelScope?.cancel()
        Dispatchers.resetMain()
    }

    @Test
    fun `offline startup opens main and does not retry sync on reconnect`() = runTest(dispatcher) {
        val connected = MutableStateFlow(false)
        every { observer.isConnected } returns connected
        viewModel = MainViewModel(observer, auth, settingsPreferences, sync)
        runCurrent()

        assertEquals(Routes.MainContainer, StartViewModel().startState.value.nexDestination)
        coVerify(exactly = 0) { sync() }
        connected.value = true
        runCurrent()
        coVerify(exactly = 0) { sync() }
    }

    @Test
    fun `silent connectivity does not block startup or login state`() = runTest(dispatcher) {
        val signal = CompletableDeferred<Boolean>()
        every { observer.isConnected } returns flow { emit(signal.await()) }
        viewModel = MainViewModel(observer, auth, settingsPreferences, sync)
        viewModel!!.checkLogin()
        runCurrent()

        assertEquals(Routes.MainContainer, StartViewModel().startState.value.nexDestination)
        assertTrue(viewModel!!.loginState.value)
        coVerify(exactly = 0) { sync() }
    }

    @Test
    fun `slow sync survives splash disposal without blocking main state`() = runTest(dispatcher) {
        every { observer.isConnected } returns MutableStateFlow(true)
        val completion = CompletableDeferred<Unit>()
        var finished = false
        coEvery { sync() } coAnswers {
            completion.await()
            finished = true
        }
        val start = StartViewModel()
        viewModel = MainViewModel(observer, auth, settingsPreferences, sync)
        runCurrent()
        start.viewModelScope.cancel()
        viewModel!!.checkLogin()
        runCurrent()

        assertTrue(viewModel!!.loginState.value)
        completion.complete(Unit)
        runCurrent()
        assertTrue(finished)
        coVerify(exactly = 1) { sync() }
    }

    @Test
    fun `sync failure preserves existing catch behavior and main remains usable`() = runTest(dispatcher) {
        every { observer.isConnected } returns MutableStateFlow(true)
        coEvery { sync() } throws IllegalStateException("Sync failed")
        viewModel = MainViewModel(observer, auth, settingsPreferences, sync)
        runCurrent()
        viewModel!!.checkLogin()
        runCurrent()

        assertTrue(viewModel!!.loginState.value)
        coVerify(exactly = 1) { sync() }
    }
}
