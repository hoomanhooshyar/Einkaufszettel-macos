package com.hooman.einkaufszettel.core.presentation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.hooman.einkaufszettel.app.Routes
import com.hooman.einkaufszettel.feature.presentation.create_list.CreateListScreenRoot
import com.hooman.einkaufszettel.feature.presentation.create_list.CreateListViewModel
import com.hooman.einkaufszettel.feature.presentation.home.HomeScreenRoot
import com.hooman.einkaufszettel.feature.presentation.home.HomeViewModel
import com.hooman.einkaufszettel.feature.presentation.list_details.ListDetailsScreenRoot
import com.hooman.einkaufszettel.feature.presentation.list_details.ListDetailsViewModel
import com.hooman.einkaufszettel.feature.presentation.login.    LoginScreenRoot
import com.hooman.einkaufszettel.feature.presentation.login.LoginViewModel
import com.hooman.einkaufszettel.feature.presentation.product.ProductScreenRoot
import com.hooman.einkaufszettel.feature.presentation.product.ProductViewModel
import com.hooman.einkaufszettel.feature.presentation.report.ReportsScreenRoot
import com.hooman.einkaufszettel.feature.presentation.report.ReportsViewModel
import com.hooman.einkaufszettel.feature.presentation.settings.SettingScreenRoot
import com.hooman.einkaufszettel.feature.presentation.settings.SettingsViewModel
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun AppNavGraph(
    navController : NavHostController,
    contentPadding: PaddingValues,
    snackBarHostState: SnackbarHostState
) {
    MaterialTheme {
        NavHost(
            navController = navController,
            startDestination = Routes.MainGraph,
        ){
            navigation<Routes.MainGraph>(
                startDestination = Routes.Home
            ){
                composable<Routes.Home> {
                    val vm = koinViewModel<HomeViewModel>()
                    HomeScreenRoot(
                        viewModel = vm,
                        contentPadding = contentPadding,
                        snackBarHostState = snackBarHostState
                    )
                }

                composable<Routes.Products> {
                    val vm = koinViewModel<ProductViewModel>()
                    ProductScreenRoot(
                        viewModel = vm,
                        contentPadding = contentPadding
                    )
                }

                composable<Routes.Reports> {
                    val vm = koinViewModel<ReportsViewModel>()
                    ReportsScreenRoot(
                        viewModel = vm,
                        contentPadding = contentPadding
                    )
                }

                composable<Routes.Settings> {
                    val vm = koinViewModel<SettingsViewModel>()
                    SettingScreenRoot(
                        viewModel = vm,
                        contentPadding = contentPadding
                    )
                }

                composable<Routes.CreateList> {
                    val vm = koinViewModel<CreateListViewModel>()
                    CreateListScreenRoot(
                        viewModel = vm,
                        onSaved = {billId -> navController.navigate(Routes.ListDetails(billId))},
                        onCancel = {navController.navigateUp()},
                        contentPadding = contentPadding
                        )
                }
                composable<Routes.ListDetails> {
                    val vm = koinViewModel<ListDetailsViewModel>()
                    ListDetailsScreenRoot(
                        viewModel = vm,
                        onBack = {navController.navigateUp()},
                        onAddProduct = {navController.navigate(Routes.Products)},
                        contentPadding = contentPadding
                    )
                }

                composable<Routes.Login> {
                    val vm = koinViewModel<LoginViewModel>()
                    LoginScreenRoot(
                        viewModel = vm,
                        navController = navController,
                        contentPadding = contentPadding,
                        snackBarHostState = snackBarHostState
                    )
                }
            }
        }

    }
}