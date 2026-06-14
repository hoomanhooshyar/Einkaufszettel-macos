package com.hooman.einkaufszettel.feature.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.hooman.einkaufszettel.core.presentation.AppDimens
import com.hooman.einkaufszettel.core.presentation.blackColor
import com.hooman.einkaufszettel.core.presentation.whiteColor

@Composable
fun CheckBoxItem(
    text: String,
    isChecked:Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    image: String?,
    textColor: Color = blackColor,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,

    ){
        Checkbox(
            checked = isChecked,
            colors = CheckboxDefaults.colors(

                checkmarkColor = whiteColor
            ),
            onCheckedChange = {
                onCheckedChange(it)
            }
        )
        ProductImage(
            modifier = Modifier
                .padding(horizontal = AppDimens.spacingMedium)
                .size(AppDimens.imageSize),
            imageUrl = image ?: "noimage",
            contentDescription = null,
        )
        Text(
            modifier = Modifier
                .padding(start = AppDimens.spacingMedium)
                .weight(1f),
            text = text,
            style = MaterialTheme.typography.titleLarge,
            color = textColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}