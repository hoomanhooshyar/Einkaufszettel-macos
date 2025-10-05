package com.hooman.einkaufszettel.data.local.db

import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask


@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
@OptIn(ExperimentalForeignApi::class)
actual class DatabaseFactory() {
    actual fun create(): RoomDatabase.Builder<AppDatabase> {
        val dbFile = documentDirectory() + "/${AppDatabase.DATABASE_NAME}"
        return Room.databaseBuilder<AppDatabase>(
            name = dbFile
        )
    }


    private fun documentDirectory(): String{
        val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null
        )
        return requireNotNull(documentDirectory?.path)
    }
}