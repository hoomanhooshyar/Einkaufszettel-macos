package com.hooman.einkaufszettel.core.di


import com.hooman.einkaufszettel.core.network.ConnectivityObserver
import com.hooman.einkaufszettel.core.network.ConnectivityObserverImpl
import com.hooman.einkaufszettel.data.local.db.DatabaseFactory
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module
    get() = module {
        single { DatabaseFactory(androidApplication()) }
        single<ConnectivityObserver> { ConnectivityObserverImpl(androidApplication()) }
    }

