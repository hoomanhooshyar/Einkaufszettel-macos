package com.hooman.einkaufszettel.core.network

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ConnectivityObserver {
    val isConnected: Flow<Boolean>
}