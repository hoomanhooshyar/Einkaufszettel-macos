package com.hooman.einkaufszettel.feature.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.doublePreferencesKey

/**
 * CE = Customized Einkaufszettel
 */

@Composable
fun CEButton(
    modifier: Modifier,
    onClick: () -> Unit,
    icon: ImageVector?,
    text: String,
    containerColor: Brush,
    contentColor: Color

) {
    Button(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor=Color.Transparent)
    ){
        Box(
            modifier = Modifier
                .background(containerColor, shape = RoundedCornerShape(8.dp))
                .then(modifier),
            contentAlignment = Alignment.Center,

        ){
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                if(icon != null){
                    Icon(
                        modifier = Modifier.size(20.dp),
                        imageVector = icon,
                        contentDescription = null,
                        tint = contentColor
                        )
                    Spacer(
                        modifier = Modifier.width(16.dp)
                    )
                }

                Text(
                    modifier = Modifier.padding(vertical = 8.dp),
                    text = text,
                    fontSize = 20.sp,
                    color = contentColor
                )
            }

        }
    }
}