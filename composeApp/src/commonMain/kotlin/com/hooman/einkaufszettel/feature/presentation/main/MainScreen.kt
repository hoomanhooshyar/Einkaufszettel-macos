package com.hooman.einkaufszettel.feature.presentation.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hooman.einkaufszettel.app.Routes
import com.hooman.einkaufszettel.core.presentation.AppNavGraph
import com.hooman.einkaufszettel.core.presentation.blackColor
import com.hooman.einkaufszettel.core.presentation.darkGreenColor
import com.hooman.einkaufszettel.core.presentation.darkYellowColor
import com.hooman.einkaufszettel.core.presentation.greenColor
import com.hooman.einkaufszettel.core.presentation.whiteColor
import com.hooman.einkaufszettel.feature.presentation.login.LoginScreenRoot
import com.hooman.einkaufszettel.feature.presentation.main.components.BottomBar
import com.hooman.einkaufszettel.feature.presentation.main.utils.BottomItems
import com.hooman.einkaufszettel.feature.presentation.main.utils.shouldShowBottomBar

import kotlinx.serialization.json.JsonNull.content
import org.koin.compose.viewmodel.koinViewModel
import org.koin.dsl.koinApplication


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = koinViewModel()
) {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

    val snackBarHostState = remember{ SnackbarHostState() }

    val isConnected by viewModel.isConnected.collectAsStateWithLifecycle()



    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if(isConnected) "Online" else "Offline"
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigateUp()
                        }
                    ){
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = ""
                        )
                    }
                },
                colors = TopAppBarColors(
                    containerColor = darkYellowColor,
                    scrolledContainerColor = whiteColor,
                    navigationIconContentColor = blackColor,
                    titleContentColor = blackColor,
                    actionIconContentColor = blackColor
                )
            )
        },
        bottomBar = {
            BottomBar(
                navController = navController,
                items = BottomItems.default()
            )
        },
        floatingActionButton = {
            if(currentRoute == Routes.Home::class.qualifiedName || currentRoute == Routes.Products::class.qualifiedName){
                FloatingActionButton(
                    onClick = {
                        if(currentRoute == Routes.Home::class.qualifiedName){
                            navController.navigate(Routes.CreateList)
                        }else if(currentRoute == Routes.Products::class.qualifiedName){
                            navController.navigate(Routes.ListDetails("1"))
                        }

                    },
                    shape = CircleShape,
                    containerColor = darkGreenColor,
                    contentColor = whiteColor,
                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(
                        defaultElevation = 8.dp
                    )
                ){
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add"
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackBarHostState)}
    ) { padding ->
        AppNavGraph(
            navController = navController,
            contentPadding = padding,
            snackBarHostState = snackBarHostState
        )

        //LoginScreenRoot(
          //  navController = rememberNavController(),
         //   contentPadding = androidx.compose.foundation.layout.PaddingValues(8.dp),
         //   snackBarHostState = snackBarHostState
        //)
    }
}