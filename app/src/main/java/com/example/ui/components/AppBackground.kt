package com.example.ui.components

import com.example.ui.theme.AppColors

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.EmochiPrimary
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class StarParticle(
    val xRatio: Float,
    val yRatio: Float,
    val radiusPx: Float,
    val baseAlpha: Float,
    val phaseOffset: Float,
    val twinkleSpeed: Float,
    val color: Color
)

private data class AuroraBlob(
    val xRatio: Float,
    val yRatio: Float,
    val radiusRatio: Float,
    val color: Color,
    val phaseOffset: Float,
    val driftX: Float,
    val driftY: Float
)

@Composable
fun AppBackground(
    modifier: Modifier = Modifier,
    baseColor: Color = AppColors.Hex_0A0A14
) {
    // Deterministic stars — two layers: distant dim + closer bright
    val stars = remember {
        val rand = Random(42)
        val list = mutableListOf<StarParticle>()
        val colors = listOf(
            AppColors.White,
            AppColors.PurpleSoft,
            AppColors.Hex_BAE6FD,
            AppColors.YellowLight,
            Color(0xFFF0ABFC) // faint pink
        )
        // Distant layer — many tiny dim stars
        for (i in 0 until 55) {
            list.add(StarParticle(
                xRatio = rand.nextFloat(),
                yRatio = rand.nextFloat(),
                radiusPx = rand.nextFloat() * 1.2f + 0.8f,
                baseAlpha = rand.nextFloat() * 0.18f + 0.06f,
                phaseOffset = rand.nextFloat() * (2f * PI.toFloat()),
                twinkleSpeed = 0.5f + rand.nextFloat() * 0.8f,
                color = colors[i % colors.size]
            ))
        }
        // Foreground layer — fewer, larger, brighter
        for (i in 0 until 25) {
            list.add(StarParticle(
                xRatio = rand.nextFloat(),
                yRatio = rand.nextFloat(),
                radiusPx = rand.nextFloat() * 2.5f + 1.5f,
                baseAlpha = rand.nextFloat() * 0.28f + 0.12f,
                phaseOffset = rand.nextFloat() * (2f * PI.toFloat()),
                twinkleSpeed = 0.3f + rand.nextFloat() * 0.5f,
                color = colors[i % colors.size]
            ))
        }
        list
    }

    // Aurora blobs — slow drifting nebula clouds
    val auroraBlobs = remember {
        listOf(
            AuroraBlob(0.25f, 0.15f, 0.70f, Color(0xFF7C3AED), 0f,      +0.08f, +0.05f),
            AuroraBlob(0.75f, 0.30f, 0.55f, Color(0xFF4C1D95), 1.2f,    -0.06f, +0.04f),
            AuroraBlob(0.50f, 0.65f, 0.80f, Color(0xFF1E1B4B), 2.4f,    +0.04f, -0.07f),
            AuroraBlob(0.10f, 0.55f, 0.45f, Color(0xFF5B21B6), 3.6f,    +0.07f, +0.03f),
            AuroraBlob(0.85f, 0.75f, 0.50f, Color(0xFF6D28D9), 4.8f,    -0.05f, -0.06f),
        )
    }

    val infinite = rememberInfiniteTransition(label = "bg")

    // Slow aurora drift time (full cycle ~20s)
    val auroraTime by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "auroraTime"
    )

    // Twinkle time (~4.5s cycle)
    val twinkleTime by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(4500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "twinkleTime"
    )

    // Central glow pulse (2.8s breathe)
    val centralPulse by infinite.animateFloat(
        initialValue = 0.08f,
        targetValue = 0.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "centralPulse"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(baseColor)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            if (w <= 0f || h <= 0f) return@Canvas

            // ── Layer 1: Deep base vignette ─────────────────────────────────
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF0A061A),
                        Color(0xFF070510)
                    ),
                    center = Offset(w * 0.5f, h * 0.5f),
                    radius = w * 0.85f
                ),
                size = size
            )

            // ── Layer 2: Aurora nebula blobs ─────────────────────────────────
            auroraBlobs.forEachIndexed { idx, blob ->
                val phase = auroraTime + blob.phaseOffset
                val driftX = sin(phase * blob.driftX * 4f) * w * 0.06f
                val driftY = cos(phase * blob.driftY * 4f) * h * 0.05f
                val cx = blob.xRatio * w + driftX
                val cy = blob.yRatio * h + driftY
                val radius = blob.radiusRatio * w
                val alpha = 0.06f + 0.04f * sin(phase + idx)

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            blob.color.copy(alpha = alpha * 1.8f),
                            blob.color.copy(alpha = alpha * 0.6f),
                            Color.Transparent
                        ),
                        center = Offset(cx, cy),
                        radius = radius
                    ),
                    radius = radius,
                    center = Offset(cx, cy)
                )
            }

            // ── Layer 3: Central top hero glow (breathing) ───────────────────
            val heroCenter = Offset(w * 0.5f, h * 0.18f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        EmochiPrimary.copy(alpha = centralPulse),
                        Color(0xFF5B21B6).copy(alpha = centralPulse * 0.4f),
                        Color.Transparent
                    ),
                    center = heroCenter,
                    radius = w * 0.85f
                ),
                center = heroCenter,
                radius = w * 0.85f
            )

            // ── Layer 4: Bottom accent glow ──────────────────────────────────
            val bottomCenter = Offset(w * 0.5f, h * 1.0f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF4C1D95).copy(alpha = 0.20f),
                        Color.Transparent
                    ),
                    center = bottomCenter,
                    radius = w * 0.70f
                ),
                center = bottomCenter,
                radius = w * 0.70f
            )

            // ── Layer 5: Stars (distant + foreground) ────────────────────────
            stars.forEach { star ->
                val px = star.xRatio * w
                val py = star.yRatio * h
                val phase = twinkleTime * star.twinkleSpeed + star.phaseOffset
                val mod = 0.65f + 0.35f * sin(phase)
                val alpha = (star.baseAlpha * mod).coerceIn(0.04f, 0.48f)

                // Tiny glow halo for larger stars
                if (star.radiusPx > 2f) {
                    drawCircle(
                        color = star.color.copy(alpha = alpha * 0.35f),
                        radius = star.radiusPx * 2.8f,
                        center = Offset(px, py)
                    )
                }
                drawCircle(
                    color = star.color.copy(alpha = alpha),
                    radius = star.radiusPx,
                    center = Offset(px, py)
                )
            }

            // ── Layer 6: Bottom vignette ─────────────────────────────────────
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(0xFF070510).copy(alpha = 0.75f)
                    ),
                    startY = h * 0.6f,
                    endY = h
                ),
                size = size
            )
        }
    }
}
