package com.hooman.einkaufszettel.core.di

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.hooman.einkaufszettel.data.local.db.AppDatabase
import org.koin.dsl.module

val testDbModule = module {
    single {
        Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
    }

    single { get<AppDatabase>().dao }
}