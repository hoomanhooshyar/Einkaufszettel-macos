package com.hooman.einkaufszettel.feature.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hooman.einkaufszettel.app.Routes
import com.hooman.einkaufszettel.core.presentation.AppDimens
import com.hooman.einkaufszettel.core.presentation.backgroundGradient
import com.hooman.einkaufszettel.core.presentation.blackColor
import com.hooman.einkaufszettel.core.presentation.greenGradient
import com.hooman.einkaufszettel.core.presentation.orangeGradient
import com.hooman.einkaufszettel.core.presentation.purpleGradient
import com.hooman.einkaufszettel.core.presentation.redGradient
import com.hooman.einkaufszettel.core.presentation.whiteColor
import com.hooman.einkaufszettel.core.util.toTwoDecimals
import com.hooman.einkaufszettel.domain.model.Bill
import com.hooman.einkaufszettel.feature.presentation.components.CETextField
import com.hooman.einkaufszettel.feature.presentation.home.components.HomeItem
import einkaufszettel.composeapp.generated.resources.Res
import einkaufszettel.composeapp.generated.resources.gesamt
import einkaufszettel.composeapp.generated.resources.no_bills
import einkaufszettel.composeapp.generated.resources.search
import einkaufszettel.composeapp.generated.resources.total
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreenRoot(
    viewModel: HomeViewModel = koinViewModel(),
    contentPadding: PaddingValues,
    snackBarHostState: SnackbarHostState,
    navController: NavHostController
) {

    val state by viewModel.state.collectAsState()


    if(state.error != null){
        val error: String = state.error!!.asString()
        LaunchedEffect(error){
            snackBarHostState.showSnackbar(error, duration = SnackbarDuration.Short)
        }
    }


    HomeScreen(
        contentPadding = contentPadding,
        background = backgroundGradient,
        bills = state.bills,
        navController = navController,
        onDelete = {
            viewModel.deleteBill(it)
        },
        totalAmount = state.totalAmount,
        onSearch = { name ->
            viewModel.searchBill(name)
        },
        searchQuery = state.searchQuery
    )
}



@Composable
fun HomeScreen(
    contentPadding: PaddingValues,
    background: Brush,
    bills: List<Bill> = emptyList(),
    navController: NavHostController,
    onDelete: (Bill) -> Unit,
    onSearch: (String) -> Unit,
    totalAmount: Double,
    searchQuery: String?
) {
    var billName by remember { mutableStateOf("") }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = background)
            .padding(contentPadding)
    ){
        Column {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()

            ) {
                Text(
                    text = stringResource(Res.string.total),
                    color = whiteColor,
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(
                    modifier = Modifier.width(8.dp)
                )
                Text(
                    text = totalAmount.toTwoDecimals(),
                    color = whiteColor,
                    style = MaterialTheme.typography.titleLarge
                )
            }
            CETextField(
                modifier = Modifier,
                value = searchQuery ?: "",
                onValueChange = {
                    onSearch(it)
                                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )

                },
                label = {
                    Text(
                        text = stringResource(Res.string.search)
                    )
                },
                placeholder = {
                    Text(
                        text = stringResource(Res.string.search) + ",,,"
                    )
                },
                readOnly = false,
                textColor = blackColor,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            )
            if(bills.isEmpty()){
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(
                        text = stringResource(Res.string.no_bills),
                        style = MaterialTheme.typography.titleLarge,
                        color = whiteColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }else{
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                ){
                    val brushes = listOf(greenGradient, orangeGradient, purpleGradient, redGradient)

                    itemsIndexed(
                        items = bills,
                        key = { _, bill -> bill.id}
                    ) { index, bill ->
                        var billIcon: ImageVector = bill.type.icon
                        val backgroundColor = brushes[index % brushes.size]

                        HomeItem(
                            modifier = Modifier
                            ,
                            bill = bill,
                            background = backgroundColor,
                            icon = billIcon,
                            onClick = {
                                navController.navigate(Routes.ListDetails(bill.id))
                            },
                            onDeleteClick = {
                                onDelete(it)
                            }
                        )
                    }
                }
            }

        }

    }

}