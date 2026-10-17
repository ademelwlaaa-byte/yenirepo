package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

private val DotColorTop    = Color(0xFFD8B4FE) // pastel violet
private val DotColorMid    = Color(0xFFA855F7) // vivid purple
private val DotColorBottom = Color(0xFF7C3AED) // deep violet

@Composable
fun TypingDots(
    dotSize: Dp = 8.dp,
    dotColor: Color = DotColorMid, // kept for API compat, not used internally
    modifier: Modifier = Modifier
) {
    val delays = listOf(0L, 180L, 360L)

    Row(
        modifier = modifier.padding(vertical = 6.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        delays.forEachIndexed { index, delayMs ->
            val offsetY  = remember { Animatable(0f) }
            val scale    = remember { Animatable(0.7f) }
            val glowAlpha = remember { Animatable(0.2f) }

            LaunchedEffect(delayMs) {
                delay(delayMs)
                // Bounce up
                offsetY.animateTo(
                    targetValue = -5f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(420, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    )
                )
            }

            LaunchedEffect(delayMs) {
                delay(delayMs)
                scale.animateTo(
                    targetValue = 1.0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(420, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    )
                )
            }

            LaunchedEffect(delayMs) {
                delay(delayMs)
                glowAlpha.animateTo(
                    targetValue = 0.85f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(420, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    )
                )
            }

            val sizePx = dotSize
            Canvas(
                modifier = Modifier
                    .size(sizePx + 4.dp) // extra room for glow
                    .padding(2.dp)
            ) {
                val cx = size.width / 2f
                val cy = size.height / 2f + offsetY.value.dp.toPx()
                val r  = (sizePx / 2f).toPx() * scale.value

                // Outer glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            DotColorMid.copy(alpha = glowAlpha.value * 0.55f),
                            Color.Transparent
                        ),
                        center = Offset(cx, cy),
                        radius = r * 2.2f
                    ),
                    radius = r * 2.2f,
                    center = Offset(cx, cy)
                )

                // Main gradient dot
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            DotColorTop,
                            DotColorMid,
                            DotColorBottom
                        ),
                        center = Offset(cx - r * 0.2f, cy - r * 0.2f),
                        radius = r * 1.4f
                    ),
                    radius = r,
                    center = Offset(cx, cy)
                )

                // Specular highlight
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.75f),
                            Color.Transparent
                        ),
                        center = Offset(cx - r * 0.25f, cy - r * 0.28f),
                        radius = r * 0.40f
                    ),
                    radius = r * 0.35f,
                    center = Offset(cx - r * 0.25f, cy - r * 0.28f)
                )
            }
        }
    }
}
