package com.minimal.snake.game

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

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    private val audioScope = CoroutineScope(Dispatchers.Default)

    // Pre-generated sound buffers for snappy zero-latency playback
    private val eatTone: ShortArray by lazy { generateSineWave(frequency = 880f, durationMs = 60, sampleRate = 44100) }
    private val turnTone: ShortArray by lazy { generateSineWave(frequency = 440f, durationMs = 25, sampleRate = 44100) }
    private val gameOverTone: ShortArray by lazy { generateDescendingTone(startFreq = 400f, endFreq = 120f, durationMs = 300, sampleRate = 44100) }
    private val startTone: ShortArray by lazy { generateAscendingTone(startFreq = 300f, endFreq = 700f, durationMs = 150, sampleRate = 44100) }

    fun playEatSound(soundEnabled: Boolean, hapticsEnabled: Boolean) {
        if (hapticsEnabled) {
            vibrate(30, VibrationEffect.DEFAULT_AMPLITUDE)
        }
        if (soundEnabled) {
            playTone(eatTone)
        }
    }

    fun playTurnSound(soundEnabled: Boolean, hapticsEnabled: Boolean) {
        if (hapticsEnabled) {
            vibrate(12, 60)
        }
        if (soundEnabled) {
            playTone(turnTone)
        }
    }

    fun playGameOverSound(soundEnabled: Boolean, hapticsEnabled: Boolean) {
        if (hapticsEnabled) {
            vibratePattern(longArrayOf(0, 80, 50, 180))
        }
        if (soundEnabled) {
            playTone(gameOverTone)
        }
    }

    fun playStartSound(soundEnabled: Boolean, hapticsEnabled: Boolean) {
        if (hapticsEnabled) {
            vibrate(20, VibrationEffect.DEFAULT_AMPLITUDE)
        }
        if (soundEnabled) {
            playTone(startTone)
        }
    }

    private fun vibrate(durationMs: Long, amplitude: Int = VibrationEffect.DEFAULT_AMPLITUDE) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = if (amplitude > 0 && amplitude <= 255) {
                    VibrationEffect.createOneShot(durationMs, amplitude)
                } else {
                    VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
                }
                vibrator?.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Exception) {}
    }

    private fun vibratePattern(timings: LongArray) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createWaveform(timings, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(timings, -1)
            }
        } catch (_: Exception) {}
    }

    private fun playTone(samples: ShortArray) {
        audioScope.launch {
            try {
                val sampleRate = 44100
                val track = AudioTrack.Builder()
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
                    .setBufferSizeInBytes(samples.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.write(samples, 0, samples.size)
                track.play()
                kotlinx.coroutines.delay((samples.size * 1000L / sampleRate) + 50)
                track.release()
            } catch (_: Exception) {}
        }
    }

    private fun generateSineWave(frequency: Float, durationMs: Int, sampleRate: Int): ShortArray {
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val angle = 2.0 * Math.PI * i * frequency / sampleRate
            val envelope = (1.0 - (i.toDouble() / numSamples)) // fade out
            buffer[i] = (sin(angle) * Short.MAX_VALUE * 0.4 * envelope).toInt().toShort()
        }
        return buffer
    }

    private fun generateAscendingTone(startFreq: Float, endFreq: Float, durationMs: Int, sampleRate: Int): ShortArray {
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(numSamples)
        var phase = 0.0
        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            val currentFreq = startFreq + (endFreq - startFreq) * progress
            phase += 2.0 * Math.PI * currentFreq / sampleRate
            val envelope = if (progress < 0.1) progress * 10.0 else (1.0 - progress)
            buffer[i] = (sin(phase) * Short.MAX_VALUE * 0.4 * envelope).toInt().toShort()
        }
        return buffer
    }

    private fun generateDescendingTone(startFreq: Float, endFreq: Float, durationMs: Int, sampleRate: Int): ShortArray {
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(numSamples)
        var phase = 0.0
        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            val currentFreq = startFreq - (startFreq - endFreq) * progress
            phase += 2.0 * Math.PI * currentFreq / sampleRate
            val envelope = (1.0 - progress)
            buffer[i] = (sin(phase) * Short.MAX_VALUE * 0.5 * envelope).toInt().toShort()
        }
        return buffer
    }
}
