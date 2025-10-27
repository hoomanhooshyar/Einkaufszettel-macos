package com.hooman.einkaufszettel.data.local.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hooman.einkaufszettel.data.local.converter.Converter
import com.hooman.einkaufszettel.data.local.dao.AppDao
import com.hooman.einkaufszettel.data.local.entity.BillEntity
import com.hooman.einkaufszettel.data.local.entity.ProductEntity
import com.hooman.einkaufszettel.data.local.entity.ShoppingItemEntity

@Database(
    entities = [BillEntity::class, ShoppingItemEntity::class, ProductEntity::class],
    version = 1
)
@TypeConverters(Converter::class)
//@ConstructedBy(DatabaseConstructor::class)
abstract class AppDatabase: RoomDatabase() {
    abstract val dao: AppDao

    companion object{
        val DATABASE_NAME = "kaufzettel_db"

    }
}