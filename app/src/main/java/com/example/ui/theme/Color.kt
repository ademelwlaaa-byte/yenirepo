package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val EmochiBackground = Color(0xFF070510)
val EmochiSurface = Color(0xFF0D0A1C)
val EmochiCard = Color(0xFF140E2D)
val EmochiCardElevated = Color(0xFF1B143B)
val EmochiBorder = Color(0xFF221A4C)
val EmochiBorderFocused = Color(0xFF7C3AED)

val EmochiPrimary = Color(0xFFA78BFA)
val EmochiPrimaryContainer = Color(0xFF2E1065)
val EmochiOnPrimary = Color(0xFFFFFFFF)

val EmochiCoralStart = Color(0xFFFFB4A2)
val EmochiCoralEnd = Color(0xFFF7967E)
val EmochiUserBubbleText = Color(0xFF21130D)

val EmochiViolet = Color(0xFFC084FC)
val EmochiCyan = Color(0xFF38BDF8)
val EmochiRose = Color(0xFFFB7185)
val EmochiGold = Color(0xFFFBBF24)

val EmochiTextPrimary = Color(0xFFF4F1FA)
val EmochiTextSecondary = Color(0xFF9E92C9)
val EmochiTextMuted = Color(0xFF6B628F)
val EmochiError = Color(0xFFF87171)
val EmochiErrorContainer = Color(0xFF3D1A1A)

object EmochiGradients {
    val primaryButton = Brush.horizontalGradient(
        colors = listOf(Color(0xFF7C3AED), Color(0xFFA855F7), Color(0xFFC084FC))
    )
    val primaryButtonVertical = Brush.verticalGradient(
        colors = listOf(Color(0xFF9333EA), Color(0xFF6D28D9))
    )
    // User mesaj balonu — sıcak derin mor
    val chatBubbleUser = Brush.linearGradient(
        colors = listOf(Color(0xFF3B1070), Color(0xFF1E0850))
    )
    val chatBubbleUserBorder = Brush.linearGradient(
        colors = listOf(Color(0xFFC084FC), Color(0xFF7C3AED), Color(0xFF4C1D95))
    )
    // AI mesaj balonu — soğuk koyu gece
    val chatBubbleAi = Brush.verticalGradient(
        colors = listOf(Color(0xFF140D2E), Color(0xFF0C0820))
    )
    val chatBubbleAiBorder = Brush.linearGradient(
        colors = listOf(Color(0xFF6D28D9), Color(0xFF3B0764), Color(0xFF1E1040))
    )
    // Input bar
    val inputBarBg = Brush.verticalGradient(
        colors = listOf(Color(0xFF120A28), Color(0xFF0C0820))
    )
    val inputBarBorder = Brush.linearGradient(
        colors = listOf(Color(0xFFA855F7), Color(0xFF4C1D95), Color(0xFF7C3AED))
    )
    // Stats bar (affection/trust/tension)
    val statsBar = Brush.horizontalGradient(
        colors = listOf(Color(0xFF1A0A30), Color(0xFF120826))
    )
    // Kart arka plan
    val cardBackground = Brush.verticalGradient(
        colors = listOf(Color(0xFF1A1040), Color(0xFF0F0A24))
    )
    val cardBorder = Brush.linearGradient(
        colors = listOf(Color(0xFFD8B4FE), Color(0xFFA855F7), Color(0xFF7C3AED), Color(0xFF4C1D95))
    )
    val heroHeader = Brush.linearGradient(
        colors = listOf(Color(0xFF1E1145), Color(0xFF0C091A), Color(0xFF070510))
    )
    val auraGlow = Brush.radialGradient(
        colors = listOf(Color(0x40A78BFA), Color(0x000F1021))
    )
    val glassBorder = Brush.linearGradient(
        colors = listOf(Color(0x60A78BFA), Color(0x2038BDF8), Color(0x102C2F54))
    )
    val goldBadge = Brush.horizontalGradient(
        colors = listOf(Color(0xFFFBBF24), Color(0xFFF59E0B))
    )
    // Quick prompt chips
    val chipBorder = Brush.linearGradient(
        colors = listOf(Color(0xFFA855F7), Color(0xFF4C1D95))
    )
    // Pill actions
    val pillBorder = Brush.linearGradient(
        colors = listOf(Color(0xFFC084FC), Color(0xFF4C1D95))
    )
    // Bottom nav indicator
    val navIndicator = Brush.verticalGradient(
        colors = listOf(Color(0x2AA855F7), Color(0x10A855F7))
    )
    // Avatar border sweep
    val avatarBorderSweep: List<Color> = listOf(
        Color(0xFFE9D5FF), Color(0xFFC084FC), Color(0xFF7C3AED), Color(0xFFE9D5FF)
    )
}

