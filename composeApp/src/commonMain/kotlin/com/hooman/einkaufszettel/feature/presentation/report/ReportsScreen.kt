package com.hooman.einkaufszettel.feature.presentation.report

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
fun ReportsScreenRoot(
    viewModel: ReportsViewModel,
    contentPadding: PaddingValues
) {
    ReportsScreen(contentPadding)
}

@Composable
fun ReportsScreen(
    contentPadding: PaddingValues
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Reports Screen",
            fontSize = 20.sp
        )
    }
}