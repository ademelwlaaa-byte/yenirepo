package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import com.example.ui.theme.AppColors
import kotlin.math.cos
import kotlin.math.sin

/**
 * Premium multi-ring loading spinner with gradient sweep and glow.
 * Replaces all CircularProgressIndicator usages.
 *
 * @param size    Outer size in dp (default 36)
 * @param color   Primary accent color (default PrimaryViolet)
 * @param small   If true, renders a compact inline version (14dp, no glow)
 */
@Composable
fun EmochiLoadingSpinner(
    modifier: Modifier = Modifier,
    size: Dp = 36.dp,
    color: Color = AppColors.PrimaryViolet,
    small: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "spinner")

    // Outer ring rotation — fast
    val outerAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "outerAngle"
    )

    // Inner ring rotation — slower, opposite direction
    val innerAngle by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "innerAngle"
    )

    // Glow pulse
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    if (small) {
        // Compact inline spinner — single arc, no glow
        Canvas(modifier = modifier.size(size)) {
            val strokeW = 2.dp.toPx()
            val arcSize = Size(this.size.width - strokeW, this.size.height - strokeW)
            val topLeft = Offset(strokeW / 2f, strokeW / 2f)

            // Track
            drawArc(
                color = color.copy(alpha = 0.2f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeW, cap = StrokeCap.Round)
            )

            // Sweep arc
            rotate(outerAngle) {
                drawArc(
                    brush = Brush.sweepGradient(
                        listOf(Color.Transparent, color, color.copy(alpha = 0.4f))
                    ),
                    startAngle = -90f,
                    sweepAngle = 240f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeW, cap = StrokeCap.Round)
                )
            }
        }
    } else {
        // Full premium spinner
        Box(
            modifier = modifier.size(size),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(size)) {
                val cx = this.size.width / 2f
                val cy = this.size.height / 2f
                val outerR = this.size.minDimension / 2f
                val strokeOuter = (size.toPx() * 0.10f).coerceAtLeast(3f)
                val strokeInner = (size.toPx() * 0.07f).coerceAtLeast(2f)

                // --- Outer glow ring ---
                drawCircle(
                    color = color.copy(alpha = glowAlpha * 0.25f),
                    radius = outerR + 4.dp.toPx(),
                    center = Offset(cx, cy),
                    style = Stroke(width = 8.dp.toPx())
                )

                // --- Outer arc track ---
                val outerTopLeft = Offset(strokeOuter / 2f, strokeOuter / 2f)
                val outerArcSize = Size(
                    this.size.width - strokeOuter,
                    this.size.height - strokeOuter
                )
                drawArc(
                    color = color.copy(alpha = 0.15f),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = outerTopLeft,
                    size = outerArcSize,
                    style = Stroke(width = strokeOuter, cap = StrokeCap.Round)
                )

                // --- Outer sweep arc ---
                rotate(outerAngle, pivot = Offset(cx, cy)) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            colorStops = arrayOf(
                                0.0f to Color.Transparent,
                                0.5f to color.copy(alpha = 0.5f),
                                0.75f to color,
                                1.0f to color.copy(alpha = 0.3f)
                            ),
                            center = Offset(cx, cy)
                        ),
                        startAngle = -90f,
                        sweepAngle = 270f,
                        useCenter = false,
                        topLeft = outerTopLeft,
                        size = outerArcSize,
                        style = Stroke(width = strokeOuter, cap = StrokeCap.Round)
                    )
                }

                // --- Outer arc tip dot glow ---
                val tipRadians = Math.toRadians((outerAngle - 90f).toDouble())
                val tipR = outerR - strokeOuter / 2f
                val tipX = cx + tipR * cos(tipRadians).toFloat()
                val tipY = cy + tipR * sin(tipRadians).toFloat()
                drawCircle(
                    color = color,
                    radius = strokeOuter * 0.7f,
                    center = Offset(tipX, tipY)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.6f),
                    radius = strokeOuter * 0.35f,
                    center = Offset(tipX, tipY)
                )

                // --- Inner arc (reverse, shorter) ---
                val innerPad = outerR * 0.32f
                val innerTopLeft = Offset(innerPad + strokeInner / 2f, innerPad + strokeInner / 2f)
                val innerArcSize = Size(
                    this.size.width - innerPad * 2 - strokeInner,
                    this.size.height - innerPad * 2 - strokeInner
                )
                val secondColor = Color(
                    red = (color.red * 0.6f + 0.4f).coerceIn(0f, 1f),
                    green = (color.green * 0.3f + 0.1f).coerceIn(0f, 1f),
                    blue = (color.blue * 0.8f + 0.2f).coerceIn(0f, 1f),
                    alpha = 1f
                )
                drawArc(
                    color = secondColor.copy(alpha = 0.12f),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = innerTopLeft,
                    size = innerArcSize,
                    style = Stroke(width = strokeInner, cap = StrokeCap.Round)
                )
                rotate(innerAngle, pivot = Offset(cx, cy)) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            colorStops = arrayOf(
                                0.0f to Color.Transparent,
                                0.6f to secondColor.copy(alpha = 0.6f),
                                0.85f to secondColor,
                                1.0f to secondColor.copy(alpha = 0.2f)
                            ),
                            center = Offset(cx, cy)
                        ),
                        startAngle = -90f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = innerTopLeft,
                        size = innerArcSize,
                        style = Stroke(width = strokeInner, cap = StrokeCap.Round)
                    )
                }

                // --- Center dot pulse ---
                drawCircle(
                    color = color.copy(alpha = glowAlpha * 0.5f),
                    radius = strokeOuter * 1.2f,
                    center = Offset(cx, cy)
                )
                drawCircle(
                    color = color.copy(alpha = 0.9f),
                    radius = strokeOuter * 0.5f,
                    center = Offset(cx, cy)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.7f),
                    radius = strokeOuter * 0.2f,
                    center = Offset(cx, cy)
                )
            }
        }
    }
}

/**
 * Full-screen loading overlay with centered spinner + optional text.
 */
@Composable
fun EmochiLoadingOverlay(
    visible: Boolean,
    text: String = "Yükleniyor...",
    color: Color = AppColors.PrimaryViolet
) {
    if (!visible) return
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xCC080614)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            EmochiLoadingSpinner(size = 52.dp, color = color)
            androidx.compose.material3.Text(
                text = text,
                color = Color(0xFFCDA8FF),
                fontSize = 14.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                letterSpacing = 0.5.sp
            )
        }
    }
}

private val Dp.toPxValue: Float get() = value * 3f // approx, real conversion in Canvas scope
