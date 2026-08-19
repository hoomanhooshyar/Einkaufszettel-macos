package com.hooman.einkaufszettel.feature.presentation.start

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.hooman.einkaufszettel.app.Routes
import com.hooman.einkaufszettel.core.presentation.AppDimens
import com.hooman.einkaufszettel.core.presentation.blackColor
import einkaufszettel.composeapp.generated.resources.Icon
import einkaufszettel.composeapp.generated.resources.Res
import einkaufszettel.composeapp.generated.resources.unknown_error
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StartScreenRoot(
    viewModel: StartViewModel = koinViewModel(),
    snackBarHostState: SnackbarHostState,
    navController: NavHostController
) {
    val state by viewModel.startState.collectAsState()
    val scope = rememberCoroutineScope()
    StartScreen(
        nextDestination = state.nexDestination,
        onStartPage = { page ->
            navController.navigate(page){
                popUpTo(Routes.Start){inclusive = true}
            }
        },
        onError = { e ->
            scope.launch {
                snackBarHostState.showSnackbar(
                    message = e, duration = SnackbarDuration.Short
                )
            }
        }
    )
}

@Composable
fun StartScreen(
    nextDestination: Any?,
    onStartPage: (Routes) -> Unit,
    onError: (String) -> Unit
) {
    val error = stringResource(Res.string.unknown_error)
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        LaunchedEffect(nextDestination){
            if(nextDestination != null){
                if(nextDestination is Routes){
                    onStartPage(nextDestination)
                }else{
                    onError(error)
                }
            }
        }

        Image(
            modifier = Modifier
                .clip(CircleShape)
                .size(150.dp),
            contentScale = ContentScale.Crop,
            painter = painterResource(Res.drawable.Icon),
            contentDescription = null
        )
        Spacer(modifier = Modifier.height(AppDimens.spacingMedium))
        CircularProgressIndicator()

    }
}