package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object BookAudioController {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var player: MediaPlayer? = null
    private var fadeJob: Job? = null

    val isPlaying: Boolean get() = player?.isPlaying == true

    fun play(context: Context, rawResId: Int, loop: Boolean = true, volume: Float = 0.85f, fadeInMs: Long = 1500) {
        fadeJob?.cancel()
        releasePlayer()

        try {
            val afd = context.resources.openRawResourceFd(rawResId) ?: return
            val p = MediaPlayer()
            p.setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            p.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            afd.close()
            p.isLooping = loop
            p.prepare()
            p.setVolume(0f, 0f)
            p.start()
            player = p

            // Fade in
            fadeJob = scope.launch {
                val steps = 30
                val stepDelay = fadeInMs / steps
                for (i in 1..steps) {
                    val v = (i.toFloat() / steps) * volume
                    try { p.setVolume(v, v) } catch (_: Exception) { break }
                    delay(stepDelay)
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("BookAudioController", "play() error", e)
            releasePlayer()
        }
    }

    fun stop(fadeOutMs: Long = 1200) {
        val p = player ?: return
        fadeJob?.cancel()
        fadeJob = scope.launch {
            val steps = 30
            val stepDelay = fadeOutMs / steps
            for (i in steps downTo 0) {
                val v = (i.toFloat() / steps)
                try { p.setVolume(v, v) } catch (_: Exception) { break }
                delay(stepDelay)
            }
            try {
                if (p.isPlaying) p.stop()
                p.release()
            } catch (_: Exception) {}
            if (player === p) player = null
        }
    }

    fun toggle(context: Context, rawResId: Int, loop: Boolean = true, volume: Float = 0.85f) {
        if (isPlaying) stop() else play(context, rawResId, loop, volume)
    }

    fun release() {
        fadeJob?.cancel()
        releasePlayer()
    }

    private fun releasePlayer() {
        try {
            player?.let { p ->
                if (p.isPlaying) p.stop()
                p.release()
            }
        } catch (_: Exception) {}
        player = null
    }
}
