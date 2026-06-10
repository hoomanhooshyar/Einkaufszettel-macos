package com.hooman.einkaufszettel.domain.model

import androidx.compose.runtime.Composable
import einkaufszettel.composeapp.generated.resources.Res
import einkaufszettel.composeapp.generated.resources.cloth
import einkaufszettel.composeapp.generated.resources.friends
import einkaufszettel.composeapp.generated.resources.house
import einkaufszettel.composeapp.generated.resources.party
import einkaufszettel.composeapp.generated.resources.supermarket

import org.jetbrains.compose.resources.stringResource

@Composable
fun PurchaseType.getDisplayTypename(): String{
    return when(this){
        PurchaseType.SUPERMARKET -> stringResource(Res.string.supermarket)
        PurchaseType.PARTY -> stringResource(Res.string.party)
        PurchaseType.HOUSE -> stringResource(Res.string.house)
        PurchaseType.FRIENDS -> stringResource(Res.string.friends)
        PurchaseType.CLOTH -> stringResource(Res.string.cloth)
        PurchaseType.OTHER -> stringResource(Res.string.cloth)
    }
}