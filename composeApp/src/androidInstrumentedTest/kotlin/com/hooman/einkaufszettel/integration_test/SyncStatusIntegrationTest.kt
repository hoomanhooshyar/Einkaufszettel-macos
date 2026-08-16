package com.hooman.einkaufszettel.integration_test

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.runComposeUiTest
import androidx.navigation.compose.rememberNavController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hooman.einkaufszettel.core.di.platformModule
import com.hooman.einkaufszettel.core.di.repositoryModule
import com.hooman.einkaufszettel.core.di.testDbModule
import com.hooman.einkaufszettel.core.di.useCaseModule
import com.hooman.einkaufszettel.core.di.viewModelModule
import com.hooman.einkaufszettel.core.presentation.backgroundGradient
import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.data.local.dao.AppDao
import com.hooman.einkaufszettel.data.local.dao.FakeAppDao
import com.hooman.einkaufszettel.data.local.entity.SyncStatus
import com.hooman.einkaufszettel.data.repository.LocalRepositoryImpl
import com.hooman.einkaufszettel.domain.model.Bill
import com.hooman.einkaufszettel.domain.model.User
import com.hooman.einkaufszettel.domain.repository.AuthRepository
import com.hooman.einkaufszettel.domain.repository.LocalRepository
import com.hooman.einkaufszettel.domain.usecase.SyncDatabaseUseCase
import com.hooman.einkaufszettel.feature.presentation.create_bill.CreateBillScreen
import com.hooman.einkaufszettel.feature.presentation.create_bill.CreateBillScreenRoot
import com.hooman.einkaufszettel.feature.presentation.create_bill.CreateBillViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.inject
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.assertEquals


@RunWith(AndroidJUnit4::class)
class SyncStatusIntegrationTest: KoinTest {

    @get:Rule
    val composeTestRule = createComposeRule()

   private lateinit var dao: FakeAppDao
    private val syncUseCase: SyncDatabaseUseCase by inject()
    private lateinit var repository: LocalRepository
    private lateinit var dynamicTestModule: Module



    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup(){
        val fakeAuth = object: AuthRepository {
            override fun getCurrentUserId(): String? {
                return "test_123"
            }

            override suspend fun signInWithGoogle(
                idToken: String,
                accessToken: String?
            ): Resource<User> {
                TODO("Not yet implemented")
            }

            override suspend fun getCurrentUser(): User? {
                return User(
                    id = "123",
                    name = "User",
                    imageUrl = null
                )
            }

            override suspend fun signOut() {
                TODO("Not yet implemented")
            }

        }
        dao = FakeAppDao()
        repository = LocalRepositoryImpl(dao)
        dynamicTestModule = module {
            single<AppDao> {dao}
            single<LocalRepository> {repository}
            single<AuthRepository> { fakeAuth }
        }

        loadKoinModules(listOf(dynamicTestModule, viewModelModule))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown(){

        unloadKoinModules(listOf(dynamicTestModule, viewModelModule, useCaseModule))
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun verifySyncStatusFlow_fromPendingToSuccess() {
        var isSavedCalled = false
        var billId = ""
        val snackbarHostState = SnackbarHostState()
        val viewModel: CreateBillViewModel = get()
        composeTestRule.setContent {
            val navController = rememberNavController()
            CreateBillScreenRoot(
                viewModel = viewModel,
                contentPadding = PaddingValues(),
                snackBarHostState = snackbarHostState,
                navController = navController,
                onCancel = {},
                onSaved = {bill ->
                    println("✅ دیباگ تست: onSaved در لایه تست اجرا شد! آیدی: ${bill?.id}")
                    if(bill != null){
                        billId = bill.id
                    }
                }
            )
        }
        println("✅ دیباگ تست: شروع تایپ در فیلد...")
        composeTestRule.onNodeWithTag("bill_name_input").performClick()
        composeTestRule.onNodeWithTag("bill_name_input").performTextInput("Party")

        composeTestRule.waitForIdle()

        println("✅ تست: کلیک روی دکمه ذخیره...")
        composeTestRule.onNodeWithTag("bill_save_button").performClick()

        println("✅ تست: منتظر ماندن برای تغییر وضعیت...")
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            billId.isNotEmpty()
        }

        runBlocking {
            assert(billId.isNotEmpty()){"Bill id was not captured!"}
            withTimeout(timeMillis = 3000){
                val savedBill = repository.getBillById(billId).first{it.data != null}

                assert(savedBill.data != null) { "Bill was not found in the database!" }

                assertEquals("Party", savedBill.data?.name)
                assertEquals(SyncStatus.LSL, savedBill.data?.syncStatus)
            }

        }

    }
}