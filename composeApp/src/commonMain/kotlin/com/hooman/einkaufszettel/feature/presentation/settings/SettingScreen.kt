package com.hooman.einkaufszettel.feature.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil3.compose.LocalPlatformContext
import com.hooman.einkaufszettel.app.Routes
import com.hooman.einkaufszettel.core.presentation.AppDimens
import com.hooman.einkaufszettel.core.presentation.UiText
import com.hooman.einkaufszettel.core.presentation.backgroundGradient
import com.hooman.einkaufszettel.core.presentation.greenGradient
import com.hooman.einkaufszettel.core.presentation.redColor
import com.hooman.einkaufszettel.core.presentation.redGradient
import com.hooman.einkaufszettel.core.presentation.whiteColor
import com.hooman.einkaufszettel.core.util.changeLanguage
import com.hooman.einkaufszettel.domain.model.User
import com.hooman.einkaufszettel.feature.presentation.components.CEButton
import com.hooman.einkaufszettel.feature.presentation.login.util.GoogleAuthManager
import com.hooman.einkaufszettel.feature.presentation.settings.components.LSBox
import com.hooman.einkaufszettel.feature.presentation.settings.components.LanSelector
import com.hooman.einkaufszettel.feature.presentation.settings.components.UserCard
import einkaufszettel.composeapp.generated.resources.Res
import einkaufszettel.composeapp.generated.resources.english
import einkaufszettel.composeapp.generated.resources.german
import einkaufszettel.composeapp.generated.resources.google40
import einkaufszettel.composeapp.generated.resources.google_login_fail
import einkaufszettel.composeapp.generated.resources.google_logo
import einkaufszettel.composeapp.generated.resources.login
import einkaufszettel.composeapp.generated.resources.login_24px
import einkaufszettel.composeapp.generated.resources.logout
import einkaufszettel.composeapp.generated.resources.logout_24px
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.koinInject

@Composable
fun SettingScreenRoot(
    viewModel: SettingsViewModel,
    contentPadding: PaddingValues,
    navController: NavHostController,
    authManager: GoogleAuthManager = koinInject(),
    snackBarHostState: SnackbarHostState
) {

    val state by viewModel.settingState.collectAsState()
    val user = state.user
    var isLoggedIn = state.isLoggedIn
    val scope = rememberCoroutineScope()
    val platformContext = LocalPlatformContext.current
    val googleTokenFailMessage = UiText.StringResourceId(Res.string.google_login_fail).asString()

    LaunchedEffect(user){
        if(user != null){
            isLoggedIn = true
        }
    }
    SettingScreen(
        contentPadding = contentPadding,
        onButtonClick = { languageCode ->
            viewModel.onLanguageSelected(languageCode)
        },
        languageCode = state.currentLanguage,
        isLoggedIn = isLoggedIn,
        user = user,
        onLogoutClick = {
            viewModel.userLogout()
        },
        onLoginClick = {
            scope.launch {
                try {
                    val idToken = authManager.signIn(platformContext)
                    if(idToken != null){
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
fun SettingScreen(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    languageCode: String,
    onButtonClick:(String) -> Unit,
    isLoggedIn: Boolean,
    user: User?,
    onLogoutClick: () -> Unit,
    onLoginClick: () -> Unit
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundGradient),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if(isLoggedIn){
            if(user != null){
                UserCard(
                    modifier = Modifier.padding(contentPadding),
                    user = user,
                    onButtonClick = {
                        onLogoutClick()
                    },
                    icon = vectorResource(Res.drawable.logout_24px),
                    buttonText = stringResource(Res.string.logout),
                    buttonBackground = redGradient
                )
            }

        }else{
            val nullUser = User(
                id = ""
            )
            UserCard(
                modifier = Modifier.padding(contentPadding),
                user = nullUser,
                onButtonClick = {
                    onLoginClick()
                },
                icon = painterResource(Res.drawable.google40),
                buttonText = stringResource(Res.string.login),
                buttonBackground = greenGradient
            )
        }
        LSBox(
            modifier = Modifier.padding(contentPadding),
            languageCode = languageCode,
            onLanguageSelected = {
                onButtonClick(it)
            }
        )


    }
}