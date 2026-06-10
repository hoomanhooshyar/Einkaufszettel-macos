package com.hooman.einkaufszettel.feature.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hooman.einkaufszettel.core.presentation.blackColor
import com.hooman.einkaufszettel.core.presentation.whiteColor

/**
 * CE = Customized Einkaufszettel
 */
@Composable
fun CETextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    trailingIcon: @Composable () -> Unit,
    label: @Composable () -> Unit,
    placeholder: @Composable () -> Unit,
    readOnly: Boolean = false,
    textColor: Color = blackColor,
    containerColor: Color = whiteColor,
    labelColor: Color = blackColor,
    keyboardType: KeyboardType,
    imeAction: ImeAction = ImeAction.Next,
    textStyle: TextStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
) {
    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                vertical = 16.dp,
                horizontal = 8.dp
            ),
        textStyle = textStyle,
        readOnly = readOnly,
        colors = TextFieldDefaults.colors(
            disabledTextColor = blackColor,
            disabledTrailingIconColor = blackColor,
            disabledLabelColor = blackColor,
            disabledPlaceholderColor = blackColor,
            focusedTextColor = textColor,
            focusedContainerColor = containerColor,
            focusedLabelColor = labelColor,

        ),
        value = value,
        onValueChange = onValueChange,
        trailingIcon = trailingIcon,
        placeholder = placeholder,
        label = label,
        shape = RoundedCornerShape(8.dp),
        singleLine = true,
        maxLines = 1,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        )

    )
}