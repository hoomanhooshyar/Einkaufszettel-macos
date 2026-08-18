package com.hooman.einkaufszettel.feature.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.LocalPlatformContext
import com.hooman.einkaufszettel.app.Routes
import com.hooman.einkaufszettel.core.presentation.UiText
import com.hooman.einkaufszettel.core.presentation.backgroundGradient
import com.hooman.einkaufszettel.core.presentation.blackColor
import com.hooman.einkaufszettel.core.presentation.greenColor
import com.hooman.einkaufszettel.core.presentation.whiteColor
import com.hooman.einkaufszettel.feature.presentation.login.components.LSButton
import com.hooman.einkaufszettel.feature.presentation.login.components.LSText
import com.hooman.einkaufszettel.feature.presentation.login.util.GoogleAuthManager

import einkaufszettel.composeapp.generated.resources.Res
import einkaufszettel.composeapp.generated.resources.apple_40


import einkaufszettel.composeapp.generated.resources.apple_sign_in
import einkaufszettel.composeapp.generated.resources.email
import einkaufszettel.composeapp.generated.resources.fail_in_login
import einkaufszettel.composeapp.generated.resources.google40
import einkaufszettel.composeapp.generated.resources.google_login_fail

import einkaufszettel.composeapp.generated.resources.google_sign_in
import einkaufszettel.composeapp.generated.resources.login
import einkaufszettel.composeapp.generated.resources.password
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun LoginScreenRoot(
    viewModel: LoginViewModel = koinViewModel(),
    authManager: GoogleAuthManager = koinInject(),
    navController: NavController,
    contentPadding: PaddingValues,
    snackBarHostState: SnackbarHostState
) {
    val loginState by viewModel.loginState.collectAsStateWithLifecycle()
    val platformContext = LocalPlatformContext.current

    val errorMessage = loginState.error

    val isLoggedIn = loginState.isLoggedIn

    val coroutineScope = rememberCoroutineScope()

    val googleTokenFailMessage = UiText.StringResourceId(Res.string.google_login_fail).asString()

    LaunchedEffect(errorMessage){
        if(!errorMessage.isNullOrEmpty()){
            snackBarHostState.showSnackbar(
                errorMessage, duration = SnackbarDuration.Short
            )

            viewModel.clearMessage()
        }
    }

    LaunchedEffect(isLoggedIn){
        if(isLoggedIn){
            navController.navigate(Routes.Settings){
                popUpTo(Routes.Login){inclusive = true}
            }

            viewModel.resetLoginStatus()
        }
    }

    LoginScreen(
        modifier = Modifier.padding(contentPadding),
        snackBarHostState = snackBarHostState,
        onAppleSignInClick = {},
        onGoogleSignInClick = {
            coroutineScope.launch {
                try {
                    val idToken = authManager.signIn(platformContext)

                    if (idToken != null){
                        viewModel.onGoogleIdTokenReceived(idToken)
                    }else{
                        snackBarHostState.showSnackbar(googleTokenFailMessage, duration = SnackbarDuration.Short)
                    }

                }catch (e: Exception){
                    e.printStackTrace()
                    snackBarHostState.showSnackbar(e.message ?: "Unknown Error", duration = SnackbarDuration.Short)
                }
            }
        }
    )
}

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    snackBarHostState: SnackbarHostState,
    onGoogleSignInClick: () -> Unit = {},
    onAppleSignInClick: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()

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


                LSButton(
                    text = stringResource(Res.string.google_sign_in),
                    textColor = blackColor,
                    backgroundColor = whiteColor,
                    logo = painterResource(Res.drawable.google40),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onGoogleSignInClick()
                    }
                )
            }
        }
    }
}