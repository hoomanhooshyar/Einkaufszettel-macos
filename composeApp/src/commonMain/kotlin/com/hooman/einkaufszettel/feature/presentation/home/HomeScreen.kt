package com.hooman.einkaufszettel.feature.presentation.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.util.TableInfo
import com.hooman.einkaufszettel.core.presentation.darkYellowColor
import com.hooman.einkaufszettel.core.presentation.whiteColor
import com.hooman.einkaufszettel.domain.model.Bill
import com.hooman.einkaufszettel.domain.model.PurchaseType
import com.hooman.einkaufszettel.domain.model.ShoppingItem
import com.hooman.einkaufszettel.feature.presentation.home.components.HomeItem
import einkaufszettel.composeapp.generated.resources.Res
import einkaufszettel.composeapp.generated.resources.gesamt
import einkaufszettel.composeapp.generated.resources.no_bills
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.random.Random

@Composable
fun HomeScreenRoot(
    viewModel: HomeViewModel = koinViewModel(),
    contentPadding: PaddingValues,
    snackBarHostState: SnackbarHostState
) {



    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF007EA7),
            Color(0xFF00B4A5)
        )
    )

    LaunchedEffect(Unit){
        viewModel.observeBills()
    }

    HomeScreen(
        contentPadding = contentPadding,
        background = gradient,
        bills = viewModel.state.collectAsState().value.bills
    )
}



@Composable
fun HomeScreen(
    contentPadding: PaddingValues,
    background: Brush,
    bills: List<Bill> = emptyList()
) {

    val priceState: MutableState<String> = remember {
        mutableStateOf("0.0")
    }

    val greenGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0XFF5FE36D),
            Color(0XFF32C85A)
        )
    )

    val purpleGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0XFFB085F5),
            Color(0XFF7E57C2)
        )
    )

    val orangeGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0XFFFFD36E),
            Color(0XFFFF9F40)
        )
    )

    val redGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFFF8A80),
            Color(0xFFFF5252)
        )
    )


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
                    text = stringResource(Res.string.gesamt),
                    color = whiteColor,
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(
                    modifier = Modifier.width(8.dp)
                )
                Text(
                    text = priceState.value,
                    color = whiteColor,
                    style = MaterialTheme.typography.titleLarge
                )
            }
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
                                .padding(8.dp)
                            ,
                            bill = bill,
                            background = backgroundColor,
                            icon = billIcon,
                            onClick = {}
                        )
                    }
                }
            }

        }

    }

}