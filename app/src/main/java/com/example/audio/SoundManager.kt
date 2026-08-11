package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

class SoundManager(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.Default)
    private var isSoundEnabled = true
    private var isHapticsEnabled = true

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    fun updateSettings(sound: Boolean, haptics: Boolean) {
        this.isSoundEnabled = sound
        this.isHapticsEnabled = haptics
    }

    fun playTapSound() {
        if (!isSoundEnabled) return
        scope.launch {
            playTone(800f, 1200f, 60)
        }
        triggerHaptic(30)
    }

    fun playCoinSound() {
        if (!isSoundEnabled) return
        scope.launch {
            playTone(987.77f, 1318.51f, 100) // B5 to E6 arpeggio
        }
        triggerHaptic(40)
    }

    fun playPowerUpSound() {
        if (!isSoundEnabled) return
        scope.launch {
            playTone(523.25f, 1046.50f, 200) // C5 to C6 rising fanfare
        }
        triggerHaptic(70)
    }

    fun playGameOverSound() {
        if (!isSoundEnabled) return
        scope.launch {
            playTone(400f, 150f, 350) // Low crash sweep
        }
        triggerHaptic(180)
    }

    private fun triggerHaptic(durationMs: Long) {
        if (!isHapticsEnabled) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Exception) {}
    }

    private fun playTone(startFreq: Float, endFreq: Float, durationMs: Int) {
        try {
            val sampleRate = 22050
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / numSamples
                val currentFreq = startFreq + (endFreq - startFreq) * t
                val angle = 2.0 * Math.PI * currentFreq * (i.toDouble() / sampleRate)
                val envelope = (1.0 - t) // Fade out
                val sampleValue = (sin(angle) * 32767 * envelope * 0.4).toInt()
                buffer[i] = sampleValue.coerceIn(-32768, 32767).toShort()
            }

            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
            // Release track after duration
            scope.launch {
                kotlinx.coroutines.delay(durationMs + 100L)
                try {
                    audioTrack.stop()
                    audioTrack.release()
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {}
    }
}
