package com.hooman.einkaufszettel.domain

sealed interface DataError {
    enum class Remote:DataError {
        REQUEST_TIMEOUT,
        TOO_MANY_REQUESTS,
        NO_INTERNET,
        SERVER,
        SERIALIZATION,
        UNKNOWN
    }

    enum class Local:DataError {
        DISK_FULL,
        UNKNOWN
    }
}