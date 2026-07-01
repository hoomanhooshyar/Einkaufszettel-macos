package com.hooman.einkaufszettel.core.presentation.animations

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.hooman.einkaufszettel.core.presentation.whiteColor
import kotlin.random.Random


data class ParticleData(
    val x: Float,
    val y: Float,
    val driftX: Float,
    val driftY: Float,
    val size: Float,
)

@Composable
fun DisintegratingItemWrapper(
    isRemoving: Boolean,
    onAnimationComplete: () -> Unit,
    content: @Composable () -> Unit
) {
    //Main Animator that moves from begin to end
    val progress = remember { Animatable(0f) }
    val particles = remember {
        List(200) {

            val x = Random.nextFloat()
            val y = Random.nextFloat()

            val driftX = -Random.nextFloat() * 300f - 50f
            val driftY = -Random.nextFloat() * 300f - 50f

            val size = Random.nextFloat() * 8f + 2f

            ParticleData(x, y, driftX, driftY, size)
        }
    }
    LaunchedEffect(isRemoving) {
        if (isRemoving) {

            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 800)
            )

            onAnimationComplete()
        }
    }

    val animValue = progress.value

    Box(
        modifier = Modifier
            .graphicsLayer {
                compositingStrategy = CompositingStrategy.Offscreen
            }
            .drawWithContent {

                //At first it draws main contents
                drawContent()

                //Now Apply Disintegrating Effect from Bottom-end to Top-start
                //By changing the position, it removes the Card from Bottom-end
                if (animValue > 0f) {
                    val maskOffset = 1f - (animValue * 1.2f)

                    drawRect(
                        brush = Brush.linearGradient(
                            colors = listOf(whiteColor, Color.Transparent),
                            start = Offset(
                                x = size.width * maskOffset,
                                y = size.height * maskOffset
                            ),
                            end = Offset(
                                x = size.width * (maskOffset + 0.2f),
                                y = size.height * (maskOffset + 0.2f)
                            ),
                        ),
                        blendMode = BlendMode.DstIn
                    )
                    particles.forEach { particle ->
                        val px = particle.x * size.width
                        val py = particle.y * size.height

                        val particlePos = (particle.x + particle.y) / 2f
                        val threshold = 1f - particlePos

                        if(animValue > threshold){
                            val timeAlive = animValue - threshold
                            val currentX = px + (particle.driftX * timeAlive)
                            val currentY = py + (particle.driftY * timeAlive)
                            val alpha = (1f - (timeAlive * 0.5f)).coerceIn(0f, 1f)

                            if(alpha > 0){
                                drawCircle(
                                    color = whiteColor.copy(alpha = alpha * 0.9f),
                                    radius = particle.size,
                                    center = Offset(currentX, currentY)
                                )
                            }
                        }
                    }

                }
            }
    ) {
        content()
    }

}