package com.hooman.einkaufszettel

import android.app.Application
import com.hooman.einkaufszettel.core.di.initKoin
import org.koin.android.ext.koin.androidContext

class EInkaufApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@EInkaufApplication)
        }

    }
}