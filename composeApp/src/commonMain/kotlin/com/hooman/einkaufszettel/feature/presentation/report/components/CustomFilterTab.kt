package com.hooman.einkaufszettel.feature.presentation.report.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hooman.einkaufszettel.core.presentation.whiteColor

@Composable
fun CustomFilterTab(
    modifier: Modifier = Modifier,
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabClick: (Int) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = whiteColor.copy(alpha = 0.15f),
                shape = CircleShape
            )
            .padding(vertical = 4.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ){
        tabs.forEachIndexed { index, title ->
            val isSelected = index == selectedTabIndex

            Text(
                text = title,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable{onTabClick(index)}
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                color = if(isSelected) whiteColor else whiteColor.copy(0.6f),
                fontWeight = if(isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 15.sp
            )
        }
    }

}