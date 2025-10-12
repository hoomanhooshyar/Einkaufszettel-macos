package com.hooman.einkaufszettel.feature.presentation.product

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

@Composable
fun ProductScreenRoot(
    viewModel: ProductViewModel,
    contentPadding: PaddingValues
) {
    ProductScreen(contentPadding)
}

@Composable
fun ProductScreen(
    contentPadding: PaddingValues
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Product Screen",
            fontSize = 20.sp
        )
    }
}