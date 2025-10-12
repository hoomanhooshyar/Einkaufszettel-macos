package com.hooman.einkaufszettel.core.network

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import platform.Network.nw_path_get_status
import platform.Network.nw_path_monitor_cancel
import platform.Network.nw_path_monitor_create
import platform.Network.nw_path_monitor_set_queue
import platform.Network.nw_path_monitor_set_update_handler
import platform.Network.nw_path_monitor_start
import platform.Network.nw_path_monitor_t
import platform.Network.nw_path_status_satisfied
import platform.darwin.dispatch_get_current_queue
import platform.darwin.dispatch_get_main_queue
import platform.darwin.dispatch_queue_create

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class ConnectivityObserverImpl(): ConnectivityObserver{

    private val monitor:nw_path_monitor_t = nw_path_monitor_create()
    private val queue = dispatch_queue_create("NetworkMonitorQueue",null)

    actual override val isConnected: Flow<Boolean> get() = callbackFlow {
        nw_path_monitor_set_queue(monitor,queue)
        nw_path_monitor_set_update_handler(monitor){path ->
            val connected = nw_path_get_status(path) == nw_path_status_satisfied
            trySend(connected)
        }

        nw_path_monitor_start(monitor)
        awaitClose {
            nw_path_monitor_cancel(monitor)
        }
    }

}