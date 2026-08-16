package com.hooman.einkaufszettel.feature.presentation.settings.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import einkaufszettel.composeapp.generated.resources.Res
import einkaufszettel.composeapp.generated.resources.english
import einkaufszettel.composeapp.generated.resources.german

/**
 * LSBox => Lan Selector Box
 */

@Composable
fun LSBox(
    modifier: Modifier = Modifier,
    languageCode: String,
    onLanguageSelected: (String) -> Unit
) {
    Box(
        modifier = modifier.padding(horizontal = 16.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ){
        Column(
            modifier = Modifier
        ){
            LanSelector(
                modifier = Modifier,
                onClick = {
                    onLanguageSelected("en")

                },
                isSelected = languageCode == "en",
                lanImage = Res.drawable.english,
                lanName = Res.string.english,
            )
            Spacer(modifier = Modifier.height(8.dp))
            LanSelector(
                modifier = Modifier,
                onClick = {
                    onLanguageSelected("de")

                },
                isSelected = languageCode == "de",
                lanImage = Res.drawable.german,
                lanName = Res.string.german,
            )
        }
    }
}