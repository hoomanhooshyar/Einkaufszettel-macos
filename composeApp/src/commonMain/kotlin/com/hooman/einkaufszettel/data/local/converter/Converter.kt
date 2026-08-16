package com.hooman.einkaufszettel.data.local.converter

import androidx.room.TypeConverter
import com.hooman.einkaufszettel.data.local.entity.SyncStatus
import kotlinx.datetime.Instant

class Converter {

    @TypeConverter
    fun fromDate(date: Instant?): Long? = date?.toEpochMilliseconds()

    @TypeConverter
    fun toDate(millis: Long?): Instant? = millis?.let { Instant.fromEpochMilliseconds(it) }

    @TypeConverter
    fun fromSyncStatus(value: SyncStatus): String{
        return value.name
    }

    @TypeConverter
    fun toSyncStatus(value: String): SyncStatus{
        return SyncStatus.valueOf(value)
    }

}