package com.lockin.dashboard.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.*
import kotlin.coroutines.coroutineContext
import java.util.Random
import kotlin.math.sin

enum class Soundscape(val title: String, val icon: String, val subtitle: String) {
    NONE("Off", "🔇", "Silence"),
    GAMMA_40HZ("40Hz Gamma", "⚡", "Binaural Deep Focus"),
    RAIN("Rainfall", "🌧️", "Calming Raindrops"),
    OCEAN("Brown Noise", "🌊", "Deep Ocean Waves"),
    WHITE_NOISE("White Noise", "📻", "Noise Masking")
}

object AmbientSoundEngine {
    private const val SAMPLE_RATE = 44100
    private var audioTrack: AudioTrack? = null
    private var playbackJob: Job? = null
    private var isPlaying = false
    private var currentSoundscape = Soundscape.NONE
    private var volume = 0.5f

    fun start(soundscape: Soundscape, vol: Float = volume) {
        if (soundscape == Soundscape.NONE) {
            stop()
            return
        }
        volume = vol.coerceIn(0f, 1f)
        if (isPlaying && currentSoundscape == soundscape) return

        stop()
        currentSoundscape = soundscape
        isPlaying = true

        val minBufferSize = AudioTrack.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_STEREO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        val bufferSize = (minBufferSize * 2).coerceAtLeast(SAMPLE_RATE / 4)

        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize * 2 * 2)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        audioTrack?.play()

        playbackJob = CoroutineScope(Dispatchers.Default).launch {
            generateAudioStream(soundscape, bufferSize)
        }
    }

    private suspend fun generateAudioStream(soundscape: Soundscape, bufferSize: Int) {
        val buffer = ShortArray(bufferSize * 2) // Stereo: left, right, left, right
        var phaseL = 0.0
        var phaseR = 0.0
        var lastBrown = 0.0
        val random = Random()

        val freqL = 216.0 // Base carrier
        val freqR = 256.0 // 40Hz difference for Gamma wave entrainment

        while (isPlaying && coroutineContext.isActive) {
            for (i in 0 until bufferSize) {
                when (soundscape) {
                    Soundscape.GAMMA_40HZ -> {
                        val sampleL = sin(phaseL) * 0.45 * volume
                        val sampleR = sin(phaseR) * 0.45 * volume
                        phaseL += 2.0 * Math.PI * freqL / SAMPLE_RATE
                        phaseR += 2.0 * Math.PI * freqR / SAMPLE_RATE

                        buffer[i * 2] = (sampleL * Short.MAX_VALUE).toInt().toShort()
                        buffer[i * 2 + 1] = (sampleR * Short.MAX_VALUE).toInt().toShort()
                    }

                    Soundscape.RAIN -> {
                        // Filtered noise with droplet bursts
                        val white = (random.nextDouble() * 2.0 - 1.0)
                        val drop = if (random.nextInt(100) > 96) (random.nextDouble() * 0.4) else 0.0
                        lastBrown = (lastBrown * 0.94) + (white * 0.06) + drop
                        val sample = (lastBrown * 0.7 * volume).coerceIn(-1.0, 1.0)
                        val shortVal = (sample * Short.MAX_VALUE).toInt().toShort()
                        buffer[i * 2] = shortVal
                        buffer[i * 2 + 1] = shortVal
                    }

                    Soundscape.OCEAN -> {
                        // Deep Brown noise (low frequency rumble / ocean wave)
                        val white = (random.nextDouble() * 2.0 - 1.0)
                        lastBrown = (lastBrown + (0.02 * white)) / 1.02
                        val sample = (lastBrown * 2.5 * volume).coerceIn(-1.0, 1.0)
                        val shortVal = (sample * Short.MAX_VALUE).toInt().toShort()
                        buffer[i * 2] = shortVal
                        buffer[i * 2 + 1] = shortVal
                    }

                    Soundscape.WHITE_NOISE -> {
                        val white = (random.nextDouble() * 2.0 - 1.0) * 0.25 * volume
                        val shortVal = (white * Short.MAX_VALUE).toInt().toShort()
                        buffer[i * 2] = shortVal
                        buffer[i * 2 + 1] = shortVal
                    }

                    Soundscape.NONE -> break
                }
            }
            audioTrack?.write(buffer, 0, buffer.size)
        }
    }

    fun setVolume(vol: Float) {
        volume = vol.coerceIn(0f, 1f)
    }

    fun stop() {
        isPlaying = false
        currentSoundscape = Soundscape.NONE
        playbackJob?.cancel()
        playbackJob = null
        try {
            audioTrack?.pause()
            audioTrack?.flush()
            audioTrack?.stop()
            audioTrack?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        audioTrack = null
    }

    fun getActiveSoundscape(): Soundscape = currentSoundscape
    fun isSoundscapeActive(): Boolean = isPlaying && currentSoundscape != Soundscape.NONE
}
