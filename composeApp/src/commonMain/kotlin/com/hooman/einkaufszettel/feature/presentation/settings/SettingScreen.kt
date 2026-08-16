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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.hooman.einkaufszettel.app.Routes
import com.hooman.einkaufszettel.core.presentation.AppDimens
import com.hooman.einkaufszettel.core.presentation.backgroundGradient
import com.hooman.einkaufszettel.core.presentation.greenGradient
import com.hooman.einkaufszettel.core.presentation.redColor
import com.hooman.einkaufszettel.core.presentation.whiteColor
import com.hooman.einkaufszettel.core.util.changeLanguage
import com.hooman.einkaufszettel.domain.model.User
import com.hooman.einkaufszettel.feature.presentation.components.CEButton
import com.hooman.einkaufszettel.feature.presentation.settings.components.LSBox
import com.hooman.einkaufszettel.feature.presentation.settings.components.LanSelector
import com.hooman.einkaufszettel.feature.presentation.settings.components.UserCard
import einkaufszettel.composeapp.generated.resources.Res
import einkaufszettel.composeapp.generated.resources.english
import einkaufszettel.composeapp.generated.resources.german
import einkaufszettel.composeapp.generated.resources.login
import einkaufszettel.composeapp.generated.resources.login_24px
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun SettingScreenRoot(
    viewModel: SettingsViewModel,
    contentPadding: PaddingValues,
    navController: NavHostController
) {

    val state by viewModel.settingState.collectAsState()
    val user = state.user
    var isLoggedIn by remember { mutableStateOf(false) }
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
            isLoggedIn = false
            viewModel.userLogout()
        },
        onLoginClick = {
            navController.navigate(Routes.Login)
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
                    onLogOutClick = {
                        onLogoutClick()
                    }
                )
            }

        }else{
            CEButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(contentPadding)
                    .padding(horizontal = AppDimens.spacingMedium),
                text = stringResource(Res.string.login),
                containerColor = greenGradient,
                contentColor = whiteColor,
                icon = vectorResource(Res.drawable.login_24px),
                onClick = {
                    onLoginClick()
                }
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