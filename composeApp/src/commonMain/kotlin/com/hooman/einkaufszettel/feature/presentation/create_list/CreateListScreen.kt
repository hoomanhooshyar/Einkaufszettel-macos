package com.hooman.einkaufszettel.feature.presentation.create_list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

@Composable
fun CreateListScreenRoot(
    viewModel: CreateListViewModel,
    onSaved: (billId: String) -> Unit,
    onCancel: () -> Unit,
    contentPadding: PaddingValues
) {
    CreateListScreen(contentPadding)
}

@Composable
fun CreateListScreen(
    contentPadding: PaddingValues
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ){
        Text(
            text = "Create List",
            fontSize = 26.sp
            )
    }
}