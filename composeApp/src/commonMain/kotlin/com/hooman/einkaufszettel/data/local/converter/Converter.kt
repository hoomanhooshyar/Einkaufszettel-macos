package com.hooman.einkaufszettel.data.local.converter

import androidx.room.TypeConverter
import kotlinx.datetime.Instant

class Converter {

    @TypeConverter
    fun fromDate(date: Instant?): Long? = date?.toEpochMilliseconds()

    @TypeConverter
    fun toDate(millis: Long?): Instant? = millis?.let { Instant.fromEpochMilliseconds(it) }

}