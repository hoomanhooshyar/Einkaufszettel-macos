package com.hooman.einkaufszettel.data.mapper

import com.hooman.einkaufszettel.data.auth.GoogleAccount
import com.hooman.einkaufszettel.domain.model.User

fun GoogleAccount.toDomainUser(): User = User(
    id = token,
    name = displayName,
    imageUrl = profileImageUrl
)