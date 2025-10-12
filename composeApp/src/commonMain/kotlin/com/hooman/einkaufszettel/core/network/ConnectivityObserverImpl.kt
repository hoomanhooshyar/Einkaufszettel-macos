package com.hooman.einkaufszettel.core.network

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class ConnectivityObserverImpl: ConnectivityObserver {
    override val isConnected: Flow<Boolean>
}