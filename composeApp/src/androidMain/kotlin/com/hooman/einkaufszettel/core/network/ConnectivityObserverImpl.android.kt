package com.hooman.einkaufszettel.core.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import androidx.core.content.getSystemService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class ConnectivityObserverImpl(
    private val context: Context
): ConnectivityObserver {

    private val connectivityManager = context.getSystemService<ConnectivityManager>()!!

    actual override val isConnected: Flow<Boolean> get() = callbackFlow {
        val callback = object : NetworkCallback(){
            override fun onLost(network: Network) {
                super.onLost(network)
                trySend(false)
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                val connected = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                trySend(connected)
            }
        }
        connectivityManager.registerDefaultNetworkCallback(callback)
        trySend(
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true
        )

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }
}
