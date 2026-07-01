package com.hooman.einkaufszettel.core.presentation.animations

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.dp
import com.hooman.einkaufszettel.core.presentation.whiteColor

fun Modifier.animatedSwipedBackground(
    isChecked: Boolean,
    selectedBrush: Brush,
    unselectedBrush: Brush
) = composed {
    val progress by animateFloatAsState(
        targetValue = if (isChecked) 1f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "swipe_bg_anim"
    )

    drawBehind{
        drawRect(unselectedBrush)

        val splitX = size.width * progress

        if(progress > 0f){
            clipRect(right = splitX){
                drawRect(selectedBrush)
            }
        }

        if(progress > 0f && progress < 1f){
            val glowWidth = 50.dp.toPx()
            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        whiteColor.copy(alpha=0.6f),
                        whiteColor,
                        whiteColor.copy(alpha = 0.6f),
                        Color.Transparent
                    ),
                    startX = splitX - glowWidth / 2,
                    endX = splitX + glowWidth / 2
                ),
                topLeft = Offset(splitX - glowWidth / 2, 0f),
                size = Size(glowWidth, size.height)
            )
        }
    }
}