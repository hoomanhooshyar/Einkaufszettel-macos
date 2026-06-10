package com.hooman.einkaufszettel.core.di


import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.hooman.einkaufszettel.R
import com.hooman.einkaufszettel.core.datastore.createDataStore
import com.hooman.einkaufszettel.core.network.ConnectivityObserver
import com.hooman.einkaufszettel.core.network.ConnectivityObserverImpl
import com.hooman.einkaufszettel.data.local.dao.AppDao
import com.hooman.einkaufszettel.data.local.db.AppDatabase
import com.hooman.einkaufszettel.data.local.db.DatabaseFactory
import com.hooman.einkaufszettel.feature.presentation.login.util.GoogleAuthManager
import com.hooman.einkaufszettel.feature.presentation.login.util.GoogleAuthManagerAndroidImpl
import com.hooman.einkaufszettel.feature.presentation.login.util.GoogleSignInProvider
import com.hooman.einkaufszettel.feature.presentation.login.util.GoogleSignInProviderImpl
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module
    get() = module {
        single { DatabaseFactory(androidApplication()) }
        single<ConnectivityObserver> { ConnectivityObserverImpl(androidApplication()) }
        single<GoogleSignInProvider> {
            GoogleSignInProviderImpl(
                context = androidContext(),
                webClient = androidContext().getString(R.string.default_web_client_id)
            )
        }
        factory<GoogleAuthManager> { GoogleAuthManagerAndroidImpl() }
        single { createDataStore(androidContext()) }

        single<AppDatabase> {
            val factory = get<DatabaseFactory>()
            factory.create()
                .setDriver(BundledSQLiteDriver())
                .setQueryCoroutineContext(Dispatchers.IO)
                .build()
        }

        single<AppDao> {
            get<AppDatabase>().dao
        }
    }


