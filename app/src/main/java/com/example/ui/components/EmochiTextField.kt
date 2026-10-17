package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppColors

/**
 * Unified premium text field for Emochi.
 * - Animated gradient border on focus
 * - Subtle background shift on focus
 * - Consistent label/hint style
 */
@Composable
fun EmochiTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false
) {
    var isFocused by remember { mutableStateOf(false) }

    // Animate border width on focus
    val borderWidth by animateDpAsState(
        targetValue = if (isFocused) 1.5.dp else 1.dp,
        animationSpec = tween(200),
        label = "borderWidth"
    )

    // Gradient border: unfocused = static purple, focused = 3-color gradient
    val unfocusedBorderColor = Color(0x507C3AED)
    val gradientBrush = if (isFocused) {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFFBE185D),
                Color(0xFF9333EA),
                Color(0xFF6366F1)
            )
        )
    } else {
        Brush.linearGradient(colors = listOf(unfocusedBorderColor, unfocusedBorderColor))
    }

    val containerColor by animateColorAsState(
        targetValue = if (isFocused) Color(0x950E0622) else Color(0x800E0620),
        animationSpec = tween(200),
        label = "containerColor"
    )

    val shape = RoundedCornerShape(12.dp)

    Column(modifier = modifier) {
        if (label != null) {
            Text(
                text = label,
                color = if (isFocused) Color(0xFFCDA8FF) else AppColors.EmochiTextSecondary,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .border(brush = gradientBrush, width = borderWidth, shape = shape)
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { isFocused = it.isFocused },
                placeholder = if (placeholder != null) {
                    { Text(placeholder, color = Color(0x70C0A0E0), fontSize = 13.sp) }
                } else null,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                singleLine = singleLine,
                maxLines = maxLines,
                minLines = minLines,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                visualTransformation = visualTransformation,
                enabled = enabled,
                readOnly = readOnly,
                isError = isError,
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 13.5.sp
                ),
                shape = shape,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = containerColor,
                    unfocusedContainerColor = Color(0x800E0620),
                    disabledContainerColor = Color(0x400E0620),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    errorBorderColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFFBE185D),
                    focusedLabelColor = Color(0xFFCDA8FF),
                    unfocusedLabelColor = AppColors.EmochiTextSecondary,
                    errorTextColor = Color(0xFFFF6B6B)
                )
            )
        }
    }
}
