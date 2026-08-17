package com.hooman.einkaufszettel.feature.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hooman.einkaufszettel.core.presentation.blackColor
import com.hooman.einkaufszettel.core.presentation.whiteColor
import androidx.compose.material3.MenuAnchorType
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.hooman.einkaufszettel.core.presentation.backgroundGradient


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> CEComboBox(
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    itemLabel: @Composable (T) -> String,
    background: Brush,
    modifier: Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var _selectedItem = remember { mutableStateOf(selectedItem) }
    val dropDownShape = RoundedCornerShape(8.dp)

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {expanded = !expanded},
        modifier = modifier
    ){
        OutlinedTextField(
            value = itemLabel(selectedItem),
            onValueChange ={},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                focusedBorderColor = whiteColor,
                unfocusedBorderColor = whiteColor,
                focusedTextColor = blackColor,
                unfocusedTextColor = blackColor,
                cursorColor = blackColor,
                focusedContainerColor = whiteColor,
                unfocusedContainerColor = whiteColor
            ),
            shape = dropDownShape,
            modifier = modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable,true)
                .background(
                    background,
                    shape = dropDownShape
                )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {expanded = false},
            modifier = Modifier
                .background(
                    color = whiteColor,
                    shape = dropDownShape
                )
        ){
            items.forEach { item ->
                DropdownMenuItem(
                    trailingIcon = {

                    },
                    text = {
                        Text(
                            text = itemLabel(item),
                            color = blackColor
                        )
                    },
                    onClick = {
                        expanded = false
                        onItemSelected(item)
                    }
                )
            }

        }
    }
}