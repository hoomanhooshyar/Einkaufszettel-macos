package com.hooman.einkaufszettel.data.local.db

import androidx.room.RoomDatabaseConstructor

//@Suppress(names = ["EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING"])
//actual object DatabaseConstructor :
//    RoomDatabaseConstructor<AppDatabase> {
//        lateinit var database: AppDatabase
//    fun initializeWith(factory: DatabaseFactory){
//        if(!::database.isInitialized){
//            database = factory.create().build()
//        }
//    }
//    actual override fun initialize(): AppDatabase {
//        check(::database.isInitialized){
//            "Database not initialized"
//        }
//        return database
//    }
//
//}