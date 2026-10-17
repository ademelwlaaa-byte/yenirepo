package com.example.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.util.ImageStorageManager

/**
 * Unified bot avatar image composable.
 *
 * Handles 3 cases transparently:
 *  1. Base64 data URI  → decoded in-memory, rendered as Bitmap (no file access needed)
 *  2. file:// or content:// URI → Coil AsyncImage
 *  3. http/https URL → Coil AsyncImage
 *
 * Falls back to [fallback] composable when no valid image is available.
 */
@Composable
fun BotAvatarImage(
    avatarUrl: String?,
    contentDescription: String? = null,
    modifier: Modifier = Modifier.fillMaxSize(),
    contentScale: ContentScale = ContentScale.Crop,
    fallback: @Composable () -> Unit = {}
) {
    when {
        // Base64 data URI — decode synchronously (small avatar, already compressed)
        !avatarUrl.isNullOrBlank() && ImageStorageManager.isBase64DataUri(avatarUrl) -> {
            val bitmap: Bitmap? = remember(avatarUrl) {
                try {
                    val base64Part = avatarUrl.substringAfter(",")
                    val bytes = Base64.decode(base64Part, Base64.NO_WRAP)
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                } catch (_: Exception) { null }
            }
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = contentDescription,
                    contentScale = contentScale,
                    modifier = modifier
                )
            } else {
                fallback()
            }
        }

        // File / content / http URI — delegate to Coil
        !avatarUrl.isNullOrBlank() -> {
            AsyncImage(
                model = avatarUrl,
                contentDescription = contentDescription,
                contentScale = contentScale,
                modifier = modifier,
                error = null,
                onError = null
            )
        }

        else -> fallback()
    }
}
