package com.hooman.einkaufszettel.core.presentation

import com.hooman.einkaufszettel.domain.DataError
import einkaufszettel.composeapp.generated.resources.Res
import einkaufszettel.composeapp.generated.resources.disk_full_error
import einkaufszettel.composeapp.generated.resources.no_internet_error
import einkaufszettel.composeapp.generated.resources.request_timeout_error
import einkaufszettel.composeapp.generated.resources.serialization_error
import einkaufszettel.composeapp.generated.resources.server_error
import einkaufszettel.composeapp.generated.resources.too_many_request_error
import einkaufszettel.composeapp.generated.resources.unknown_error

fun DataError.toUiText(): UiText{
    val stringRes = when(this){
        DataError.Local.DISK_FULL -> Res.string.disk_full_error
        DataError.Local.UNKNOWN -> Res.string.unknown_error
        DataError.Remote.REQUEST_TIMEOUT -> Res.string.request_timeout_error
        DataError.Remote.TOO_MANY_REQUESTS -> Res.string.too_many_request_error
        DataError.Remote.NO_INTERNET -> Res.string.no_internet_error
        DataError.Remote.SERVER -> Res.string.server_error
        DataError.Remote.SERIALIZATION -> Res.string.serialization_error
        DataError.Remote.UNKNOWN -> Res.string.unknown_error
    }

    return UiText.StringResourceId(stringRes)
}