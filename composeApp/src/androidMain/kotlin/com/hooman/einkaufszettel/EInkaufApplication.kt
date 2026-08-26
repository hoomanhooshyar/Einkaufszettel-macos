package com.hooman.einkaufszettel

import android.app.Application
import com.hooman.einkaufszettel.core.di.initKoin
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.initialize
import org.koin.android.ext.koin.androidContext

class EInkaufApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        Firebase.initialize(this)
        initKoin {
            androidContext(this@EInkaufApplication)
        }

    }
}