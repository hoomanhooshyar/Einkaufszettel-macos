package com.hooman.einkaufszettel.feature.presentation.list_details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun ListDetailsScreenRoot(
    viewModel: ListDetailsViewModel,
    onBack: () -> Unit,
    onAddProduct: () -> Unit,
    contentPadding: PaddingValues

) {
    ListDetailsScreen(contentPadding)
}

@Composable
fun ListDetailsScreen(
    contentPadding: PaddingValues
) {
    Column(
        modifier = Modifier
            .padding(contentPadding)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = "List Details Screen"
        )
    }
}
