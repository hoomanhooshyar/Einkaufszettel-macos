package com.hooman.einkaufszettel.core.di

import com.hooman.einkaufszettel.core.network.ConnectivityObserver
import com.hooman.einkaufszettel.core.network.ConnectivityObserverImpl
import com.hooman.einkaufszettel.data.local.db.DatabaseConstructor

import com.hooman.einkaufszettel.data.local.db.DatabaseFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module
    get() = module {
        single { DatabaseFactory() }

        single<ConnectivityObserver> { ConnectivityObserverImpl() }

//        single {
//            DatabaseConstructor.initializeWith(get())
//            DatabaseConstructor.initialize()
//        }
    }