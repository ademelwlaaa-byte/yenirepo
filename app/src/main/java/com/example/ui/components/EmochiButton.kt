package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppColors
import com.example.ui.theme.EmochiGradients

private val ShapeDefault  = RoundedCornerShape(14.dp)
private val ShapePill     = RoundedCornerShape(50.dp)
private val ShapeSmall    = RoundedCornerShape(10.dp)

// Primary — vivid purple gradient, white text
@Composable
fun EmochiPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    fontSize: TextUnit = 14.sp,
    cornerRadius: Dp = 14.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 22.dp, vertical = 12.dp)
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed || !enabled) 0.95f else 1f,
        animationSpec = tween(120),
        label = "btnScale"
    )
    val shape = RoundedCornerShape(cornerRadius)
    val alpha = if (enabled) 1f else 0.45f

    Box(
        modifier = modifier
            .scale(scale)
            .clip(shape)
            .background(
                brush = if (enabled) {
                    Brush.linearGradient(
                        listOf(Color(0xFFB06CF4), Color(0xFF8B33F0), Color(0xFF5B21B6))
                    )
                } else {
                    Brush.linearGradient(
                        listOf(Color(0xFF2A1550), Color(0xFF1A0A35))
                    )
                }
            )
            .border(
                BorderStroke(
                    1.5.dp,
                    if (enabled) Brush.sweepGradient(EmochiGradients.avatarBorderSweep)
                    else Brush.linearGradient(listOf(Color(0x303B0764), Color(0x151E1035)))
                ),
                shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled && !isLoading,
                onClick = onClick
            )
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                EmochiLoadingSpinner(size = 16.dp, small = true, color = Color.White)
            } else {
                icon?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = alpha),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(
                    text = text,
                    color = Color.White.copy(alpha = alpha),
                    fontSize = fontSize,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// Secondary — transparent with gradient border, purple text
@Composable
fun EmochiSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    fontSize: TextUnit = 13.sp,
    cornerRadius: Dp = 12.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 18.dp, vertical = 10.dp)
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed || !enabled) 0.96f else 1f,
        animationSpec = tween(120),
        label = "secBtnScale"
    )
    val shape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .scale(scale)
            .clip(shape)
            .background(
                Brush.linearGradient(listOf(Color(0x1A7C3AED), Color(0x0A4C1D95)))
            )
            .border(
                BorderStroke(1.dp, EmochiGradients.inputBarBorder),
                shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = AppColors.PurpleLight.copy(alpha = if (enabled) 1f else 0.5f),
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
            }
            Text(
                text = text,
                color = AppColors.PurpleLight.copy(alpha = if (enabled) 1f else 0.5f),
                fontSize = fontSize,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// Danger — red gradient, for destructive actions
@Composable
fun EmochiDangerButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    fontSize: TextUnit = 13.sp,
    cornerRadius: Dp = 12.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 18.dp, vertical = 10.dp)
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed || !enabled) 0.96f else 1f,
        animationSpec = tween(120),
        label = "dangerBtnScale"
    )
    val shape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .scale(scale)
            .clip(shape)
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFFBE123C), Color(0xFF9F1239), Color(0xFF7F1D1D))
                )
            )
            .border(
                BorderStroke(
                    1.dp,
                    Brush.linearGradient(listOf(Color(0xFFFB7185), Color(0xFFBE123C)))
                ),
                shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = if (enabled) 1f else 0.5f),
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
            }
            Text(
                text = text,
                color = Color.White.copy(alpha = if (enabled) 1f else 0.5f),
                fontSize = fontSize,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Ghost — no background, just text + subtle border
@Composable
fun EmochiGhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textColor: Color = AppColors.TextSlate,
    fontSize: TextUnit = 13.sp,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = tween(100),
        label = "ghostScale"
    )

    Text(
        text = text,
        color = if (enabled) textColor else textColor.copy(alpha = 0.4f),
        fontSize = fontSize,
        fontWeight = FontWeight.Medium,
        modifier = modifier
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .padding(horizontal = 10.dp, vertical = 8.dp)
    )
}
