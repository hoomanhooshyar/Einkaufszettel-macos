package com.hooman.einkaufszettel.feature.presentation.add_shopping_item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
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
                message = "Checked IDs from DB: ${state.checkedProductIds}",
                duration = SnackbarDuration.Long
            )
        }
    }
    AddShoppingItemScreen(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
        products = state.products,
        checkedProductIds = state.checkedProductIds,
        onCheckedChange = { product,isChecked ->
            if(isChecked){
                val id = Uuid.random().toString()
                val shoppingItem = ShoppingItem(
                    id = id,
                    billId = viewModel.billId,
                    productId = product.id,
                    itemCount = 0,
                    productName = product.name,
                    productPrice = product.price,
                    productImage = product.image,
                    isChecked = true,
                    userId = viewModel.userId.value!!
                )
                viewModel.insertShoppingItemIntoBill(shoppingItem)
            }else{
                viewModel.removeShoppingItemByProductId(product.id)
            }
        }
    )
}

@Composable
fun AddShoppingItemScreen(
    products: List<Product> = emptyList(),
    checkedProductIds: Set<String>,
    onCheckedChange: (Product, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    if(products.isEmpty()){
        Column(
            modifier = modifier,
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
                        .fillMaxWidth()
                        .padding(8.dp),
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