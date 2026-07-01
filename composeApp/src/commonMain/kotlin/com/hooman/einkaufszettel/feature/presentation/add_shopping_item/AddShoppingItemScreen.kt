package com.hooman.einkaufszettel.feature.presentation.add_shopping_item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import com.hooman.einkaufszettel.core.presentation.AppDimens
import com.hooman.einkaufszettel.core.presentation.backgroundGradient
import com.hooman.einkaufszettel.core.presentation.blackColor
import com.hooman.einkaufszettel.core.presentation.greenGradient
import com.hooman.einkaufszettel.core.presentation.orangeGradient
import com.hooman.einkaufszettel.core.presentation.purpleGradient
import com.hooman.einkaufszettel.core.presentation.redGradient
import com.hooman.einkaufszettel.domain.model.Product
import com.hooman.einkaufszettel.domain.model.ShoppingItem
import com.hooman.einkaufszettel.feature.presentation.components.CECheckListItem
import einkaufszettel.composeapp.generated.resources.Res
import einkaufszettel.composeapp.generated.resources.no_products
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Composable
fun AddShoppingItemScreenRoot(
    viewModel: AddShoppingItemViewModel = koinViewModel(),
    contentPadding: PaddingValues,
    snackBarHostState: SnackbarHostState,
    navController: NavHostController
) {
    val state by viewModel.addBillItemState.collectAsState()

    if(state.error != null){
        val error = state.error!!.asString()
        LaunchedEffect(error){
            snackBarHostState.showSnackbar(
                message = error,
                duration = SnackbarDuration.Long
            )
        }
    }
    AddShoppingItemScreen(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
        products = state.products,
        backgroundColor = backgroundGradient,
        checkedProductIds = state.checkedProductIds,
        onCheckedChange = { product,isChecked ->
            if(isChecked){
                val id = "${viewModel.billId}_${product.id}"

                val shoppingItem = ShoppingItem(
                    id = id,
                    billId = viewModel.billId,
                    productId = product.id,
                    itemCount = 0,
                    productName = product.name,
                    productPrice = product.price,
                    productImage = product.image,
                    discount = 0f,
                    isChecked = false,
                    userId = viewModel.userId.value!!
                )
                viewModel.insertShoppingItemIntoBill(shoppingItem)
            }else{
                viewModel.removeShoppingItemByProductIdAndBillId(product.id)
            }
        }
    )
}

@Composable
fun AddShoppingItemScreen(
    products: List<Product> = emptyList(),
    checkedProductIds: Set<String>,
    backgroundColor: Brush,
    modifier: Modifier = Modifier,
    onCheckedChange: (Product, Boolean) -> Unit,

    ) {
    if(products.isEmpty()){
        Column(
            modifier = modifier
                .background(backgroundColor),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                text = stringResource(Res.string.no_products),
                style = MaterialTheme.typography.titleLarge,
                color = blackColor,
                fontWeight = FontWeight.Bold
            )
        }
    }else{
        LazyColumn(
            modifier = modifier
                .background(backgroundColor)
        ){

            val brushes = listOf(greenGradient, orangeGradient, purpleGradient, redGradient)

            itemsIndexed(
                items = products,
                key = { _, product -> product.id}
            ) { index, product ->
                val isProductChecked = checkedProductIds.contains(product.id)
                val backgroundColor = brushes[index % brushes.size]
                CECheckListItem(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = AppDimens.spacingSmall, vertical = AppDimens.spacingSmall),
                    background = backgroundColor,
                    item = product,
                    isChecked = isProductChecked,
                    onCheckedChange = { isChecked ->
                        onCheckedChange(product, isChecked)
                    }
                )
            }
        }
    }
}