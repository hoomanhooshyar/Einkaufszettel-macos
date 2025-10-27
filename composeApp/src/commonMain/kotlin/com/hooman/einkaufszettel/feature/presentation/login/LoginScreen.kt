package com.hooman.einkaufszettel.feature.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.hooman.einkaufszettel.core.presentation.backgroundGradient
import com.hooman.einkaufszettel.core.presentation.blackColor
import com.hooman.einkaufszettel.core.presentation.greenColor
import com.hooman.einkaufszettel.core.presentation.whiteColor
import com.hooman.einkaufszettel.feature.presentation.login.components.LSButton
import com.hooman.einkaufszettel.feature.presentation.login.components.LSText
import einkaufszettel.composeapp.generated.resources.Res
import einkaufszettel.composeapp.generated.resources.apple_40
import einkaufszettel.composeapp.generated.resources.apple_48
import einkaufszettel.composeapp.generated.resources.apple_logo

import einkaufszettel.composeapp.generated.resources.apple_sign_in
import einkaufszettel.composeapp.generated.resources.email
import einkaufszettel.composeapp.generated.resources.google40
import einkaufszettel.composeapp.generated.resources.google_logo

import einkaufszettel.composeapp.generated.resources.google_sign_in
import einkaufszettel.composeapp.generated.resources.login
import einkaufszettel.composeapp.generated.resources.password
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreenRoot(
    viewModel: LoginViewModel = koinViewModel(),
    navController: NavController,
    contentPadding: PaddingValues,
    snackBarHostState: SnackbarHostState
) {
    LoginScreen(
        modifier = Modifier.padding(contentPadding),
        snackBarHostState = snackBarHostState
    )
}

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    snackBarHostState: SnackbarHostState
) {
    val scope = remember { CoroutineScope(Dispatchers.Default) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(brush = backgroundGradient),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Card(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = whiteColor
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LSText(
                    text = stringResource(Res.string.email),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                LSText(
                    text = stringResource(Res.string.password),
                    modifier = Modifier.padding(8.dp),
                    isPassword = true
                )

                LSButton(
                    text = stringResource(Res.string.login),
                    textColor = whiteColor,
                    backgroundColor = greenColor,
                    modifier = Modifier.fillMaxWidth()
                ){
                    scope.launch {
                        snackBarHostState.showSnackbar(
                            message = "Login",
                            duration = SnackbarDuration.Short
                        )
                    }

                }

                LSButton(
                    text = stringResource(Res.string.google_sign_in),
                    textColor = blackColor,
                    backgroundColor = whiteColor,
                    logo = painterResource(Res.drawable.google40),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        scope.launch {
                            snackBarHostState.showSnackbar(
                                message = "Google",
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                )

                LSButton(
                    text = stringResource(Res.string.apple_sign_in),
                    textColor = whiteColor,
                    backgroundColor = blackColor,
                    logo = painterResource(Res.drawable.apple_40),
                    modifier = Modifier.fillMaxWidth()
                ){
                    scope.launch {
                        snackBarHostState.showSnackbar(
                            message = "Apple",
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            }
        }
    }
}