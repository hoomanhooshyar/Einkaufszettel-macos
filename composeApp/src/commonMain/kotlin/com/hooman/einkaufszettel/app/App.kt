package com.hooman.einkaufszettel.app


import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.hooman.einkaufszettel.core.presentation.RootNavGraph
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun App() {
    MaterialTheme {
        RootNavGraph()
    }
}