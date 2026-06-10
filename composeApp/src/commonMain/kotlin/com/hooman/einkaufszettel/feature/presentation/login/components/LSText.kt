package com.hooman.einkaufszettel.feature.presentation.login.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.sp
import com.hooman.einkaufszettel.core.presentation.blackColor


/**
 * LS = Login and Sign-up
 */
@Composable
fun LSText(
    text: String,
    isPassword: Boolean = false,
    modifier: Modifier = Modifier
) {
    var input by remember { mutableStateOf("") }
    val visual: VisualTransformation = if(isPassword)
        PasswordVisualTransformation()
    else
        VisualTransformation.None

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ){
        Text(
            text = text,
            fontSize = 16.sp,
            color = blackColor
        )

        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            visualTransformation = visual,
            leadingIcon = {
                if(isPassword)
                    Icon(
                        imageVector = Icons.Filled.Password,
                        contentDescription = "password"
                    )
                else
                    Icon(
                        imageVector = Icons.Filled.Email,
                        contentDescription = "email"
                    )
            }
        )
    }
}