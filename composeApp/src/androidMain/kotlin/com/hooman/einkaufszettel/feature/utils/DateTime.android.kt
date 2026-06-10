package com.hooman.einkaufszettel.feature.utils

import com.hooman.einkaufszettel.domain.DataError
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

actual object DateTime {
    actual fun getFormattedDate(timestamp: String): String {
        val timestampFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        val outputFormat = "MMM dd, yyyy"

        val dateFormatter = SimpleDateFormat(outputFormat, Locale.getDefault())
        dateFormatter.timeZone = TimeZone.getTimeZone("GMT")

        val parser = SimpleDateFormat(timestampFormat, Locale.getDefault())
        parser.timeZone = TimeZone.getTimeZone("GMT")

        try {
            val date = parser.parse(timestamp)
            if(date != null){
                dateFormatter.timeZone = TimeZone.getDefault()
                return dateFormatter.format(date)
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
        return timestamp
    }
}