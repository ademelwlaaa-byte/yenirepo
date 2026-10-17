package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

object MoodColors {
    fun getMoodHue(mood: String): Float {
        return when (mood.lowercase()) {
            "joy", "neşeli" -> 38f
            "warm", "sıcak" -> 340f
            "sad", "hüzünlü" -> 250f
            "tense", "gergin" -> 8f
            "curious", "meraklı" -> 275f
            else -> 200f
        }
    }

    fun getMoodLabel(mood: String): String {
        return when (mood.lowercase()) {
            "joy" -> "neşeli"
            "warm" -> "sıcak"
            "sad" -> "hüzünlü"
            "tense" -> "gergin"
            "curious" -> "meraklı"
            else -> "sakin"
        }
    }
}

@Composable
fun OrbView(
    hue: Float,
    size: Dp = 48.dp,
    modifier: Modifier = Modifier
) {
    val infinite = rememberInfiniteTransition(label = "orb_$hue")

    val rotationAngle by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbRotation"
    )

    val pulseAlpha by infinite.animateFloat(
        initialValue = 0.55f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orbPulse"
    )

    val innerPulse by infinite.animateFloat(
        initialValue = 0.70f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "innerPulse"
    )

    // Color palette from hue
    val coreColor      = Color.hsl(hue, 0.90f, 0.78f)
    val midColor       = Color.hsl(hue, 0.80f, 0.55f)
    val outerColor     = Color.hsl(hue, 0.70f, 0.35f)
    val darkEdge       = Color.hsl(hue, 0.60f, 0.12f)
    val complementHue  = (hue + 180f) % 360f
    val nebulaColor    = Color.hsl(complementHue, 0.75f, 0.60f, alpha = 0.35f)
    val analogHue      = (hue + 60f) % 360f
    val swirl1         = Color.hsl(analogHue, 0.85f, 0.65f, alpha = 0.30f)
    val ringColor      = Color.hsl(hue, 0.95f, 0.80f, alpha = 0.60f)
    val ambientGlow    = Color.hsl(hue, 0.80f, 0.55f, alpha = 0.20f)

    Canvas(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
    ) {
        val r = this.size.minDimension / 2f
        val cx = this.size.width / 2f
        val cy = this.size.height / 2f
        val center = Offset(cx, cy)

        // Layer 1: Ambient outer glow (soft, large)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    ambientGlow,
                    Color.Transparent
                ),
                center = center,
                radius = r * 1.15f
            ),
            radius = r * 1.15f,
            center = center
        )

        // Layer 2: Deep dark base
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(darkEdge, Color.hsl(hue, 0.50f, 0.06f)),
                center = center,
                radius = r
            ),
            radius = r,
            center = center
        )

        // Layer 3: Main sphere radial gradient (off-center highlight)
        val highlightOffset = Offset(cx - r * 0.30f, cy - r * 0.30f)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(coreColor, midColor, outerColor, darkEdge),
                center = highlightOffset,
                radius = r * 1.40f
            ),
            radius = r,
            center = center
        )

        // Layer 4: Rotating nebula swirl (complement hue)
        rotate(rotationAngle, pivot = center) {
            drawNebulaSwirl(center, r, nebulaColor, swirl1, pulseAlpha)
        }

        // Layer 5: Inner glow core (breathing)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    coreColor.copy(alpha = innerPulse * 0.55f),
                    Color.Transparent
                ),
                center = highlightOffset,
                radius = r * 0.65f
            ),
            radius = r * 0.60f,
            center = highlightOffset
        )

        // Layer 6: Energy ring (pulsing, sharp)
        drawCircle(
            color = ringColor.copy(alpha = pulseAlpha * 0.70f),
            radius = r * 0.82f,
            center = center,
            style = Stroke(width = 1.8.dp.toPx())
        )

        // Layer 7: Outer thin rim
        drawCircle(
            color = coreColor.copy(alpha = 0.40f),
            radius = r * 0.97f,
            center = center,
            style = Stroke(width = 0.8.dp.toPx())
        )

        // Layer 8: Specular glass highlight (large soft)
        val specOffset = Offset(cx - r * 0.28f, cy - r * 0.30f)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.70f),
                    Color.White.copy(alpha = 0.20f),
                    Color.Transparent
                ),
                center = specOffset,
                radius = r * 0.38f
            ),
            radius = r * 0.34f,
            center = specOffset
        )

        // Layer 9: Tiny sharp specular dot
        val dotOffset = Offset(cx - r * 0.34f, cy - r * 0.36f)
        drawCircle(
            color = Color.White.copy(alpha = 0.92f),
            radius = r * 0.07f,
            center = dotOffset
        )
    }
}

private fun DrawScope.drawNebulaSwirl(
    center: Offset,
    radius: Float,
    color1: Color,
    color2: Color,
    alpha: Float
) {
    val steps = 8
    for (i in 0 until steps) {
        val angle = (i.toFloat() / steps) * 360f
        val rad = Math.toRadians(angle.toDouble())
        val dist = radius * 0.45f
        val x = center.x + cos(rad).toFloat() * dist
        val y = center.y + sin(rad).toFloat() * dist
        val blobRadius = radius * (0.22f + (i % 3) * 0.06f)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    if (i % 2 == 0) color1.copy(alpha = alpha * 0.45f) else color2.copy(alpha = alpha * 0.35f),
                    Color.Transparent
                ),
                center = Offset(x, y),
                radius = blobRadius
            ),
            radius = blobRadius,
            center = Offset(x, y)
        )
    }
}
