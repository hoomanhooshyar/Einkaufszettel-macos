package com.hooman.einkaufszettel.feature.presentation.start

import androidx.lifecycle.ViewModel
import com.hooman.einkaufszettel.app.Routes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class StartViewModel: ViewModel() {
    private val _startState = MutableStateFlow(
        StartState(nexDestination = Routes.MainContainer)
    )
    val startState: StateFlow<StartState> = _startState.asStateFlow()
}
