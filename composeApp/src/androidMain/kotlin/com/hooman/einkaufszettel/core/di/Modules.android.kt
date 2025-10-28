package com.hooman.einkaufszettel.core.di


import com.hooman.einkaufszettel.R
import com.hooman.einkaufszettel.core.network.ConnectivityObserver
import com.hooman.einkaufszettel.core.network.ConnectivityObserverImpl
import com.hooman.einkaufszettel.data.local.db.DatabaseConstructor
import com.hooman.einkaufszettel.data.local.db.DatabaseFactory
import com.hooman.einkaufszettel.feature.presentation.login.util.GoogleSignInProvider
import com.hooman.einkaufszettel.feature.presentation.login.util.GoogleSignInProviderImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module
    get() = module {
        single { DatabaseFactory(androidApplication()) }
        single<ConnectivityObserver> { ConnectivityObserverImpl(androidApplication()) }
        single<GoogleSignInProvider> { GoogleSignInProviderImpl(
            context = androidContext(),
            webClient = androidContext().getString(R.string.default_web_client_id)
        ) }
//        single {
//            //DatabaseConstructor.initializeWith(get())
//            DatabaseConstructor.initialize()
//        }
    }

