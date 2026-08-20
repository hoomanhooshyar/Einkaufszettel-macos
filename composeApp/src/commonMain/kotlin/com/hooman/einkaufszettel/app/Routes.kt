package com.hooman.einkaufszettel.app

import kotlinx.serialization.Serializable


interface Routes {
    @Serializable
    data object MainGraph: Routes

    @Serializable
    data object Splash: Routes

    @Serializable
    data object Login: Routes

    @Serializable
    data object Register: Routes

    @Serializable
    data object Home: Routes

    @Serializable
    data object Products: Routes

    @Serializable
    data object Reports: Routes

    @Serializable
    data object Settings: Routes

    @Serializable
    data object CreateList: Routes

    @Serializable
    data class ListDetails(val billId: String): Routes

    @Serializable
    data class AddShoppingItem(val billId: String): Routes

    @Serializable
    data class AddProduct(val productId: String? = null): Routes

    @Serializable
    data object Start: Routes
}