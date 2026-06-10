package com.hooman.einkaufszettel.feature.presentation.product

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.hooman.einkaufszettel.app.Routes
import com.hooman.einkaufszettel.core.presentation.backgroundGradient
import com.hooman.einkaufszettel.core.presentation.greenGradient
import com.hooman.einkaufszettel.core.presentation.orangeGradient
import com.hooman.einkaufszettel.core.presentation.purpleGradient
import com.hooman.einkaufszettel.core.presentation.redGradient
import com.hooman.einkaufszettel.core.presentation.whiteColor
import com.hooman.einkaufszettel.domain.model.Product
import com.hooman.einkaufszettel.feature.presentation.product.components.ProductItem
import einkaufszettel.composeapp.generated.resources.Res
import einkaufszettel.composeapp.generated.resources.no_products
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProductScreenRoot(
    viewModel: ProductViewModel = koinViewModel(),
    snackBarHostState: SnackbarHostState,
    contentPadding: PaddingValues,
    navController: NavController
) {

    val state by viewModel.state.collectAsState()
    val deleteState by viewModel.deleteState.collectAsState()

    val deleteMessageText = deleteState?.asString()
    val errorMessageText = state.error?.asString()

    LaunchedEffect(deleteMessageText){
        if(deleteMessageText != null){
            snackBarHostState.showSnackbar(
                message = deleteMessageText,
                duration = SnackbarDuration.Short
            )
            viewModel.clearDeleteState()
        }
    }

    LaunchedEffect(errorMessageText){
        if(errorMessageText != null){
            snackBarHostState.showSnackbar(
                message = errorMessageText,
                duration = SnackbarDuration.Short
            )
            viewModel.clearError()
        }
    }

    ProductScreen(
        contentPadding = contentPadding,
        products = viewModel.state.collectAsState().value.products,
        onProductClick = {
            navController.navigate(Routes.AddProduct(it.id))
        },
        onDeleteClick = {product -> viewModel.deleteProduct(product)}
    )
}

@Composable
fun ProductScreen(
    contentPadding: PaddingValues,
    products: List<Product> = emptyList(),
    onProductClick:(product: Product) -> Unit,
    onDeleteClick: (product: Product) -> Unit
) {
    if (products.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .background(brush = backgroundGradient),
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
            modifier = Modifier
                .fillMaxSize()
                .background(brush = backgroundGradient)
                .padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,

        ) {

            val brushes = listOf(greenGradient, orangeGradient, purpleGradient, redGradient)
            itemsIndexed(
                items = products,
                key = { _, product -> product.id }
            ) { index, product ->
                val backgroundColor = brushes[index % brushes.size]
                ProductItem(
                    product = product,
                    onProductClick = {
                        onProductClick(product)
                    },
                    onDeleteClick = {
                        onDeleteClick(product)
                    },
                    backgroundColor = backgroundColor
                )
            }

        }
    }
}
