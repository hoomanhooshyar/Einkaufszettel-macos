package com.hooman.einkaufszettel.feature.presentation.shopping_item_list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import com.hooman.einkaufszettel.app.Routes
import com.hooman.einkaufszettel.core.presentation.AppDimens
import com.hooman.einkaufszettel.core.presentation.backgroundCardGradient
import com.hooman.einkaufszettel.core.presentation.backgroundGradient
import com.hooman.einkaufszettel.core.presentation.greenGradient
import com.hooman.einkaufszettel.core.presentation.orangeGradient
import com.hooman.einkaufszettel.core.presentation.premiumGrayBlueGradient
import com.hooman.einkaufszettel.core.presentation.purpleGradient
import com.hooman.einkaufszettel.core.presentation.redGradient
import com.hooman.einkaufszettel.core.presentation.whiteColor
import com.hooman.einkaufszettel.domain.model.Bill
import com.hooman.einkaufszettel.domain.model.ShoppingDetails
import com.hooman.einkaufszettel.feature.presentation.shopping_item_list.components.AddedShoppingItem
import com.hooman.einkaufszettel.feature.presentation.shopping_item_list.components.Header
import einkaufszettel.composeapp.generated.resources.Res
import einkaufszettel.composeapp.generated.resources.no_products
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.collections.forEach

@Composable
fun ShoppingListDetailsScreenRoot(
    viewModel: ShoppingListDetailsViewModel = koinViewModel(),
    snackBarHostState: SnackbarHostState,
    navController: NavHostController,
    onBack: () -> Unit,
    onAddProduct: () -> Unit,
    contentPadding: PaddingValues,
) {
    val state by viewModel.listDetailsState.collectAsState()

    if (state.error != null) {
        val error = state.error!!.asString()
        LaunchedEffect(error) {
            snackBarHostState.showSnackbar(error, duration = SnackbarDuration.Long)
        }
    }
    if (state.bill == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = whiteColor)
        }
        return
    }
    val bill = state.bill
    ShoppingListDetailsScreen(
        contentPadding = contentPadding,
        bill = bill!!,
        shoppingDetailsList = state.shoppingDetailsItems,
        onClick = {
            navController.navigate(Routes.AddShoppingItem(bill.id))
        },
        onCountChange = { shoppingItemId, count ->
            viewModel.updateShoppingItemCount(shoppingItemId, count)
        },
        onDeleteClick = { shoppingItemId ->
            viewModel.onDeleteClick(shoppingItemId)
        },
        onCheckedChange = { shoppingItemId, isChecked ->
            viewModel.onUpdateCheckedChange(shoppingItemId, isChecked)
        },
        onDiscountChange = { shoppingItemId, discount ->

            viewModel.updateDiscount(shoppingItemId, discount)

        },
        onTotalAmountChange = {
            viewModel.getTotalAmount() ?: 0.0
        }
    )
}

@Composable
fun ShoppingListDetailsScreen(
    contentPadding: PaddingValues,
    bill: Bill,
    shoppingDetailsList: List<ShoppingDetails>?,
    onClick: () -> Unit,
    onCountChange: (String, Int) -> Unit,
    onDeleteClick: (String) -> Unit,
    onCheckedChange: (String, Boolean) -> Unit,
    onDiscountChange: (String, Float) -> Unit,
    onTotalAmountChange: () -> Double
) {
    Column(
        modifier = Modifier
            .padding(contentPadding)
            .fillMaxSize()
            .background(backgroundGradient),
    ) {
        Header(
            modifier = Modifier,
            bill = bill,
            background = backgroundCardGradient,
            totalAmount = onTotalAmountChange(),
            onClick = { onClick() }
        )

        if (shoppingDetailsList.isNullOrEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(Res.string.no_products),
                    style = MaterialTheme.typography.titleLarge,
                    color = whiteColor,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                val brushes = listOf(greenGradient, orangeGradient, purpleGradient, redGradient)
                itemsIndexed(
                    items = shoppingDetailsList,
                    key = { _, shoppingDetails -> shoppingDetails.shoppingItemId }
                ) { index, shoppingDetails ->
                    val backgroundColor = brushes[index % brushes.size]
                    val (plusBrush, minusBrush) = when (backgroundColor) {
                        greenGradient -> Pair(redGradient, orangeGradient)
                        orangeGradient -> Pair(purpleGradient, greenGradient)
                        purpleGradient -> Pair(greenGradient, redGradient)
                        else -> Pair(redGradient, orangeGradient)
                    }
                    AddedShoppingItem(
                        item = shoppingDetails,
                        selectedBackground = backgroundColor,
                        unselectedBackground = premiumGrayBlueGradient,
                        modifier = Modifier.padding(AppDimens.spacingSmall),
                        plusColor = if (shoppingDetails.isChecked) premiumGrayBlueGradient else plusBrush,
                        minusColor = if (shoppingDetails.isChecked) premiumGrayBlueGradient else minusBrush,
                        onDeleteClick = { shoppingItemId ->
                            onDeleteClick(shoppingItemId)
                        },
                        onCountChange = { shoppingItemId, count ->
                            onCountChange(shoppingItemId, count)
                        },
                        isChecked = shoppingDetails.isChecked,
                        onCheckedChange = { shoppingItemId, isChecked ->
                            onCheckedChange(shoppingItemId, isChecked)
                        },
                        onDiscountChange = { shoppingItemId, discount ->
                            onDiscountChange(shoppingItemId, discount)
                        }
                    )
                }
            }
        }
    }
}
