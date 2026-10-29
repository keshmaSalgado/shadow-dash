package com.shadowdash.game.audio

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * Procedural audio synthesizer that produces high quality, original sound effects
 * and ambient music as valid 16-bit PCM WAV files, ensuring the game is completely
 * self-contained with zero missing external audio files.
 */
object SoundGenerator {
    private const val SAMPLE_RATE = 44100

    fun prepareAudioFiles(context: Context): Map<String, File> {
        val audioDir = File(context.cacheDir, "shadow_audio")
        if (!audioDir.exists()) {
            audioDir.mkdirs()
        }

        val soundMap = mutableMapOf<String, File>()

        // 1. Jump Sound (Upward pitch chirp)
        soundMap["jump"] = getOrCreateWav(audioDir, "jump.wav") {
            generateJumpSound()
        }

        // 2. Slide Sound (Whoosh friction sound)
        soundMap["slide"] = getOrCreateWav(audioDir, "slide.wav") {
            generateSlideSound()
        }

        // 3. Blue Crystal Chime (Crisp ethereal chime)
        soundMap["crystal_blue"] = getOrCreateWav(audioDir, "crystal_blue.wav") {
            generateCrystalSound(pitch = 1046.5) // C6
        }

        // 4. Purple Crystal Chime (Rare high resonant chord)
        soundMap["crystal_purple"] = getOrCreateWav(audioDir, "crystal_purple.wav") {
            generateCrystalSound(pitch = 1318.5) // E6
        }

        // 5. Shield Powerup (Harmonic shimmer)
        soundMap["powerup_shield"] = getOrCreateWav(audioDir, "powerup_shield.wav") {
            generateShieldPowerupSound()
        }

        // 6. Magnet Powerup (Electric magnetic buzz chime)
        soundMap["powerup_magnet"] = getOrCreateWav(audioDir, "powerup_magnet.wav") {
            generateMagnetSound()
        }

        // 7. Shield Break (Shatter dispersion)
        soundMap["shield_break"] = getOrCreateWav(audioDir, "shield_break.wav") {
            generateShieldBreakSound()
        }

        // 8. Collision / Game Over (Impact thud)
        soundMap["collision"] = getOrCreateWav(audioDir, "collision.wav") {
            generateCollisionSound()
        }

        // 9. UI Click
        soundMap["click"] = getOrCreateWav(audioDir, "click.wav") {
            generateClickSound()
        }

        // 10. Ambient Music Loop (Ethereal night forest soundscape)
        soundMap["ambient_music"] = getOrCreateWav(audioDir, "ambient_forest.wav") {
            generateAmbientForestMusic()
        }

        return soundMap
    }

    private fun getOrCreateWav(dir: File, fileName: String, generator: () -> ShortArray): File {
        val file = File(dir, fileName)
        if (!file.exists() || file.length() == 0L) {
            val samples = generator()
            writeWavFile(file, samples, SAMPLE_RATE)
        }
        return file
    }

    private fun generateJumpSound(): ShortArray {
        val durationSec = 0.22f
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val samples = ShortArray(numSamples)
        var phase = 0.0

        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            // Exponential pitch rise from 280Hz to 720Hz
            val freq = 280.0 + 440.0 * progress * progress
            phase += 2.0 * PI * freq / SAMPLE_RATE
            val envelope = (1.0 - progress) * (1.0 - exp(-progress * 25.0))
            val wave = (sin(phase) + 0.3 * sin(2.0 * phase)) * envelope
            samples[i] = (wave.coerceIn(-1.0, 1.0) * 24000).toInt().toShort()
        }
        return samples
    }

    private fun generateSlideSound(): ShortArray {
        val durationSec = 0.35f
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val samples = ShortArray(numSamples)
        var noise = 0.0

        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            val envelope = sin(progress * PI)
            // Filtered white noise with low rumble
            val whiteNoise = (Math.random() * 2.0 - 1.0)
            noise = noise * 0.85 + whiteNoise * 0.15
            val rumble = sin(2.0 * PI * 110.0 * i / SAMPLE_RATE)
            val wave = (noise * 0.7 + rumble * 0.3) * envelope
            samples[i] = (wave.coerceIn(-1.0, 1.0) * 20000).toInt().toShort()
        }
        return samples
    }

    private fun generateCrystalSound(pitch: Double): ShortArray {
        val durationSec = 0.4f
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val samples = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val decay = exp(-t * 9.0)
            // Bell-like fundamental + harmonic overtones
            val f1 = sin(2.0 * PI * pitch * t)
            val f2 = 0.4 * sin(2.0 * PI * pitch * 2.75 * t)
            val f3 = 0.2 * sin(2.0 * PI * pitch * 5.4 * t)
            val wave = (f1 + f2 + f3) * decay
            samples[i] = (wave.coerceIn(-1.0, 1.0) * 26000).toInt().toShort()
        }
        return samples
    }

    private fun generateShieldPowerupSound(): ShortArray {
        val durationSec = 0.6f
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val samples = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val progress = i.toDouble() / numSamples
            val baseFreq = 300.0 + 500.0 * progress
            val decay = (1.0 - progress * 0.5)
            val wave = (sin(2.0 * PI * baseFreq * t) +
                    0.5 * sin(2.0 * PI * baseFreq * 1.5 * t) +
                    0.25 * sin(2.0 * PI * baseFreq * 2.0 * t)) * decay
            samples[i] = (wave.coerceIn(-1.0, 1.0) * 22000).toInt().toShort()
        }
        return samples
    }

    private fun generateMagnetSound(): ShortArray {
        val durationSec = 0.5f
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val samples = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val vibrato = sin(2.0 * PI * 18.0 * t) * 40.0
            val freq = 520.0 + vibrato
            val decay = exp(-t * 5.0)
            val wave = sin(2.0 * PI * freq * t) * decay
            samples[i] = (wave.coerceIn(-1.0, 1.0) * 22000).toInt().toShort()
        }
        return samples
    }

    private fun generateShieldBreakSound(): ShortArray {
        val durationSec = 0.35f
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val samples = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val noise = Math.random() * 2.0 - 1.0
            val decay = exp(-t * 12.0)
            val tone = sin(2.0 * PI * 800.0 * (1.0 - t * 2.0) * t)
            val wave = (noise * 0.7 + tone * 0.3) * decay
            samples[i] = (wave.coerceIn(-1.0, 1.0) * 25000).toInt().toShort()
        }
        return samples
    }

    private fun generateCollisionSound(): ShortArray {
        val durationSec = 0.45f
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val samples = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val decay = exp(-t * 8.0)
            val freq = 160.0 * exp(-t * 6.0)
            val rumble = sin(2.0 * PI * freq * t)
            val noise = (Math.random() * 2.0 - 1.0) * exp(-t * 18.0)
            val wave = (rumble * 0.75 + noise * 0.25) * decay
            samples[i] = (wave.coerceIn(-1.0, 1.0) * 28000).toInt().toShort()
        }
        return samples
    }

    private fun generateClickSound(): ShortArray {
        val durationSec = 0.05f
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val samples = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val decay = exp(-t * 60.0)
            val wave = sin(2.0 * PI * 1200.0 * t) * decay
            samples[i] = (wave.coerceIn(-1.0, 1.0) * 18000).toInt().toShort()
        }
        return samples
    }

    private fun generateAmbientForestMusic(): ShortArray {
        // 8 seconds smooth looping mystic ambient soundscape
        val durationSec = 8.0f
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val samples = ShortArray(numSamples)

        // Pentatonic minor progression chords: Am9, Fmaj7, C, G6
        val chordFrequencies = listOf(
            doubleArrayOf(220.0, 261.63, 329.63, 493.88), // Am9
            doubleArrayOf(174.61, 220.0, 261.63, 329.63), // Fmaj7
            doubleArrayOf(261.63, 329.63, 392.0, 493.88), // Cmaj7
            doubleArrayOf(196.0, 246.94, 293.66, 392.0)   // G6
        )

        val chordDuration = numSamples / 4

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val chordIndex = (i / chordDuration) % 4
            val localProgress = (i % chordDuration).toDouble() / chordDuration
            val envelope = sin(localProgress * PI) // smooth swell in and out

            val freqs = chordFrequencies[chordIndex]
            var sumWave = 0.0
            for (f in freqs) {
                // Warm pad synthesis with slow chorus vibrato
                val vibrato = sin(2.0 * PI * 0.5 * t) * 1.5
                val w = sin(2.0 * PI * (f + vibrato) * t) +
                        0.3 * sin(4.0 * PI * f * t)
                sumWave += w
            }
            sumWave = (sumWave / freqs.size) * envelope * 0.45

            // Add gentle high mystical sparkle
            val shimmer = sin(2.0 * PI * 880.0 * t) * 0.05 * sin(t * 1.5)
            val finalSample = (sumWave + shimmer).coerceIn(-1.0, 1.0)
            samples[i] = (finalSample * 18000).toInt().toShort()
        }
        return samples
    }

    private fun writeWavFile(file: File, samples: ShortArray, sampleRate: Int) {
        val totalAudioLen = samples.size * 2
        val totalDataLen = totalAudioLen + 36
        val byteRate = sampleRate * 2

        val header = ByteArray(44)
        val buffer = ByteBuffer.wrap(header).order(ByteOrder.LITTLE_ENDIAN)

        buffer.put("RIFF".toByteArray())
        buffer.putInt(totalDataLen)
        buffer.put("WAVE".toByteArray())
        buffer.put("fmt ".toByteArray())
        buffer.putInt(16) // Subchunk1Size (16 for PCM)
        buffer.putShort(1) // AudioFormat (1 = PCM)
        buffer.putShort(1) // NumChannels (1 = Mono)
        buffer.putInt(sampleRate)
        buffer.putInt(byteRate)
        buffer.putShort(2) // BlockAlign (channels * bitsPerSample / 8)
        buffer.putShort(16) // BitsPerSample
        buffer.put("data".toByteArray())
        buffer.putInt(totalAudioLen)

        FileOutputStream(file).use { out ->
            out.write(header)
            val audioBuffer = ByteBuffer.allocate(samples.size * 2).order(ByteOrder.LITTLE_ENDIAN)
            for (s in samples) {
                audioBuffer.putShort(s)
            }
            out.write(audioBuffer.array())
        }
    }
}

