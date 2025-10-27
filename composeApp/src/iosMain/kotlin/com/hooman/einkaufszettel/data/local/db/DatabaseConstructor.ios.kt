package com.hooman.einkaufszettel.data.local.db

import androidx.room.RoomDatabaseConstructor

@Suppress(names = ["EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING"])
actual object DatabaseConstructor :
    RoomDatabaseConstructor<AppDatabase> {
        lateinit var factory: DatabaseFactory
        fun initializeWith(factory: DatabaseFactory){
            if(!::factory.isInitialized){
                this.factory = factory
            }
        }
    actual override fun initialize(): AppDatabase {
        return factory.create().build()
    }
}