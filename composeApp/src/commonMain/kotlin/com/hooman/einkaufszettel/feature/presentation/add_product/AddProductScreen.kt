package com.hooman.einkaufszettel.feature.presentation.add_product

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hooman.einkaufszettel.core.presentation.AppDimens
import com.hooman.einkaufszettel.core.presentation.UiText
import com.hooman.einkaufszettel.core.presentation.backgroundGradient
import com.hooman.einkaufszettel.core.presentation.blackColor
import com.hooman.einkaufszettel.core.presentation.greenGradient
import com.hooman.einkaufszettel.core.presentation.whiteColor
import com.hooman.einkaufszettel.domain.model.Product
import com.hooman.einkaufszettel.feature.presentation.add_product.components.ImageGridPicker
import com.hooman.einkaufszettel.feature.presentation.components.CEButton
import com.hooman.einkaufszettel.feature.presentation.components.CETextField
import einkaufszettel.composeapp.generated.resources.Res
import einkaufszettel.composeapp.generated.resources.add_item
import einkaufszettel.composeapp.generated.resources.data_save_just_in_local
import einkaufszettel.composeapp.generated.resources.fill_all_fields
import einkaufszettel.composeapp.generated.resources.item_added_successfully
import einkaufszettel.composeapp.generated.resources.payments_24px
import einkaufszettel.composeapp.generated.resources.product_added_successfully
import einkaufszettel.composeapp.generated.resources.product_name
import einkaufszettel.composeapp.generated.resources.product_price
import einkaufszettel.composeapp.generated.resources.save
import einkaufszettel.composeapp.generated.resources.stylus_24px
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Composable
fun AddProductScreenRoot(
    viewModel: AddProductViewModel = koinViewModel(),
    contentPadding: PaddingValues,
    navController: NavHostController,
    snackBarHostState: SnackbarHostState
) {
    val state by viewModel.addProductState.collectAsState()
    val scope = rememberCoroutineScope()

    if(state.error != null){
        val message = state.error!!.asString()
        LaunchedEffect(message){
            snackBarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            viewModel.clearError()
        }
    }



    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
                AddProductScreen(
                    icons = state.productIcons,
                    background = backgroundGradient,
                    onSaveClick = { productName, productPrice, productImage ->
                        viewModel.addProduct(
                            productName = productName,
                            productPrice = productPrice,
                            productImage = productImage
                        )
                        navController.popBackStack()
                    },
                    showMessage = { message ->
                        scope.launch {
                            snackBarHostState.showSnackbar(message, duration = SnackbarDuration.Short)
                        }
                    },
                    oldProduct = state.oldProduct
                )

        if(state.isLoading){
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(blackColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ){
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

        }
    }
}

@OptIn(ExperimentalUuidApi::class)
@Composable
fun AddProductScreen(
    icons: List<String>,
    background: Brush,
    onSaveClick:(
            productName: String,
            productPrice: String,
            productImage: String
            ) -> Unit,
    showMessage: (String) -> Unit,
    oldProduct: Product?
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = background)
            .padding(top = AppDimens.spacingMedium)

    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            var selectedImageStr by remember { mutableStateOf<String?>(null) }
            var productName by remember { mutableStateOf("") }
            var productPrice by remember { mutableStateOf("") }
            val successMessage = stringResource(Res.string.product_added_successfully)
            val fillAllFields = stringResource(Res.string.fill_all_fields)
            LaunchedEffect(oldProduct){
                if(oldProduct != null){
                    selectedImageStr = oldProduct.image
                    productName = oldProduct.name
                    productPrice = oldProduct.price.toString()
                }
            }
            ImageGridPicker(
                productIcons = icons, // 👈 اسم ورودی را در کامپوننتی که نوشتیم icons گذاشته بودیم
                selectedIcon = selectedImageStr,
                onIconSelected = { selectedIcon ->
                    selectedImageStr = selectedIcon
                },
                modifier = Modifier
                    .size(60.dp)
                    .padding(top = AppDimens.spacingSmall)
            )
            CETextField(
                modifier = Modifier,
                value = productName,
                onValueChange = {productName = it},
                trailingIcon = {
                    Icon(
                        painter = painterResource(Res.drawable.stylus_24px),
                        contentDescription = null
                    )
                },
                label = {
                    Text(
                        text = stringResource(Res.string.product_name)
                    )
                },
                placeholder = {
                    Text(
                        text = stringResource(Res.string.product_name)
                    )
                },
                keyboardType = KeyboardType.Text
            )

            CETextField(
                modifier = Modifier,
                value = productPrice,
                onValueChange = {productPrice = it},
                trailingIcon = {
                    Icon(
                        painter = painterResource(Res.drawable.payments_24px),
                        contentDescription = null
                    )
                },
                label = {
                    Text(
                        text = stringResource(Res.string.product_price)
                    )
                },
                placeholder = {
                    Text(
                        text = stringResource(Res.string.product_price)
                    )
                },
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            )

            CEButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = 8.dp,
                        horizontal = 16.dp
                    ),
                onClick = {
                    if(selectedImageStr != null &&
                        productName.isNotBlank() &&
                        productPrice.isNotBlank()){
                    onSaveClick(
                        productName,
                        productPrice,
                        selectedImageStr!!
                    )
                }else{
                        showMessage(fillAllFields)
                    }

                },
                icon = Icons.Default.Save,
                text = stringResource(Res.string.save),
                containerColor = greenGradient,
                contentColor = whiteColor
            )
        }
    }
}