package com.hooman.einkaufszettel.core.presentation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.hooman.einkaufszettel.app.Routes
import com.hooman.einkaufszettel.feature.presentation.add_product.AddProductScreenRoot
import com.hooman.einkaufszettel.feature.presentation.add_product.AddProductViewModel
import com.hooman.einkaufszettel.feature.presentation.add_shopping_item.AddShoppingItemScreenRoot
import com.hooman.einkaufszettel.feature.presentation.add_shopping_item.AddShoppingItemViewModel
import com.hooman.einkaufszettel.feature.presentation.create_bill.CreateBillScreenRoot
import com.hooman.einkaufszettel.feature.presentation.create_bill.CreateBillViewModel
import com.hooman.einkaufszettel.feature.presentation.home.HomeScreenRoot
import com.hooman.einkaufszettel.feature.presentation.home.HomeViewModel
import com.hooman.einkaufszettel.feature.presentation.shopping_item_list.ShoppingListDetailsScreenRoot
import com.hooman.einkaufszettel.feature.presentation.shopping_item_list.ShoppingListDetailsViewModel
import com.hooman.einkaufszettel.feature.presentation.login.    LoginScreenRoot
import com.hooman.einkaufszettel.feature.presentation.login.LoginViewModel
import com.hooman.einkaufszettel.feature.presentation.product.ProductScreenRoot
import com.hooman.einkaufszettel.feature.presentation.product.ProductViewModel
import com.hooman.einkaufszettel.feature.presentation.report.ReportsScreenRoot
import com.hooman.einkaufszettel.feature.presentation.report.ReportsViewModel
import com.hooman.einkaufszettel.feature.presentation.settings.SettingScreenRoot
import com.hooman.einkaufszettel.feature.presentation.settings.SettingsViewModel
import com.hooman.einkaufszettel.feature.presentation.start.StartScreenRoot
import com.hooman.einkaufszettel.feature.presentation.start.StartViewModel
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun AppNavGraph(
    navController : NavHostController,
    contentPadding: PaddingValues,
    snackBarHostState: SnackbarHostState,
    isLogin:Boolean = false
) {
    MaterialTheme {

        NavHost(
            navController = navController,
            startDestination = Routes.MainGraph,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = {fullWidth -> fullWidth},
                    animationSpec = tween(durationMillis = 400)
                ) + fadeIn(animationSpec = tween(durationMillis = 400))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = {fullWidth -> -fullWidth},
                    animationSpec = tween(durationMillis = 400)
                ) + fadeOut(animationSpec = tween(durationMillis = 400))
            },

            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = {fullWidth -> fullWidth},
                    animationSpec = tween(durationMillis = 400)
                ) + fadeIn(animationSpec = tween(durationMillis = 400))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = {fullWidth -> -fullWidth},
                    animationSpec = tween(durationMillis = 400)
                ) + fadeOut(animationSpec = tween(durationMillis = 400))
            }

        ){
            navigation<Routes.MainGraph>(
                startDestination = Routes.Start,

            ){

                composable<Routes.Start> {
                    val vm = koinViewModel<StartViewModel>()
                    StartScreenRoot(
                        viewModel = vm,
                        snackBarHostState = snackBarHostState,
                        navController = navController
                    )
                }

                composable<Routes.Home> {
                    val vm = koinViewModel<HomeViewModel>()
                    HomeScreenRoot(
                        viewModel = vm,
                        contentPadding = contentPadding,
                        snackBarHostState = snackBarHostState,
                        navController = navController
                    )
                }

                composable<Routes.Products> {
                    val vm = koinViewModel<ProductViewModel>()
                    ProductScreenRoot(
                        viewModel = vm,
                        contentPadding = contentPadding,
                        snackBarHostState = snackBarHostState,
                        navController = navController
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
                        contentPadding = contentPadding,
                        navController = navController,
                        snackBarHostState = snackBarHostState
                    )
                }

                composable<Routes.CreateList> {
                    val vm = koinViewModel<CreateBillViewModel>()
                    CreateBillScreenRoot(
                        viewModel = vm,
                        onSaved = {bill -> navController.navigate(Routes.ListDetails(bill!!.id))},
                        onCancel = {navController.navigateUp()},
                        contentPadding = contentPadding,
                        snackBarHostState = snackBarHostState,
                        navController = navController
                        )
                }
                composable<Routes.ListDetails> {
                    val vm = koinViewModel<ShoppingListDetailsViewModel>()
                    ShoppingListDetailsScreenRoot(
                        viewModel = vm,
                        onBack = {navController.navigateUp()},
                        onAddProduct = {navController.navigate(Routes.Products)},
                        contentPadding = contentPadding,
                        snackBarHostState = snackBarHostState,
                        navController = navController
                    )
                }

                composable<Routes.AddShoppingItem> {
                    val vm = koinViewModel<AddShoppingItemViewModel>()
                    AddShoppingItemScreenRoot(
                        viewModel = vm,
                        contentPadding = contentPadding,
                        snackBarHostState = snackBarHostState,
                        navController = navController
                    )
                }

                composable<Routes.AddProduct> {
                    val vm = koinViewModel<AddProductViewModel>()
                    AddProductScreenRoot(
                        viewModel = vm,
                        contentPadding = contentPadding,
                        navController = navController,
                        snackBarHostState = snackBarHostState
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