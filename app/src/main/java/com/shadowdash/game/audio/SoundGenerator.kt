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
        val twilightWav = getOrCreateWav(audioDir, "ambient_forest.wav") {
            generateAmbientForestMusic()
        }
        soundMap["twilight_reverie"] = twilightWav
        soundMap["ambient_music"] = twilightWav

        // 11. Neon Velocity (Synthwave cyber pulse 128 BPM)
        soundMap["neon_velocity"] = getOrCreateWav(audioDir, "neon_velocity.wav") {
            generateNeonVelocityMusic()
        }

        // 12. Starfall Sanctuary (Celestial nocturnal piano & bell chimes 96 BPM)
        soundMap["starfall_sanctuary"] = getOrCreateWav(audioDir, "starfall_sanctuary.wav") {
            generateStarfallSanctuaryMusic()
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

    private fun generateNeonVelocityMusic(): ShortArray {
        // 7.5 seconds (16 beats at 128 BPM) synthwave arpeggio loop
        val durationSec = 7.5f
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val samples = ShortArray(numSamples)

        val beatDuration = durationSec / 16.0
        val sixteenthDuration = beatDuration / 4.0

        // Chord progression: Dm -> Bb -> F -> C
        val arpeggioNotes = listOf(
            doubleArrayOf(146.83, 220.0, 293.66, 349.23, 440.0, 587.33), // Dm
            doubleArrayOf(116.54, 174.61, 233.08, 293.66, 349.23, 466.16), // Bb
            doubleArrayOf(174.61, 220.0, 261.63, 349.23, 440.0, 523.25), // F
            doubleArrayOf(130.81, 164.81, 196.0, 261.63, 329.63, 392.0)   // C
        )

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val sixteenthIndex = (t / sixteenthDuration).toInt()
            val measureIndex = (sixteenthIndex / 16) % 4
            val stepInMeasure = sixteenthIndex % 16

            val notes = arpeggioNotes[measureIndex]
            val noteFreq = notes[stepInMeasure % notes.size]
            val timeInStep = t - (sixteenthIndex * sixteenthDuration)
            val pluckDecay = exp(-timeInStep * 18.0)

            // Synth lead arpeggiator (sine + harmonics)
            val lead = (sin(2.0 * PI * noteFreq * t) +
                    0.4 * sin(4.0 * PI * noteFreq * t) +
                    0.2 * sin(6.0 * PI * noteFreq * t)) * pluckDecay * 0.35

            // Driving bass pulse on 8th notes
            val bassStep = (stepInMeasure / 2) % 2
            val bassFreq = notes[0] * 0.5
            val bassDecay = exp(-(t % (beatDuration / 2.0)) * 12.0)
            val bass = (sin(2.0 * PI * bassFreq * t) + 0.3 * sin(4.0 * PI * bassFreq * t)) * bassDecay * 0.38

            // Subtle cyber kick drum on quarter beats
            val timeInBeat = t % beatDuration
            val kickDecay = exp(-timeInBeat * 28.0)
            val kickFreq = (110.0 * exp(-timeInBeat * 35.0)).coerceAtLeast(45.0)
            val kick = sin(2.0 * PI * kickFreq * timeInBeat) * kickDecay * 0.45

            val mixed = (lead + bass + kick).coerceIn(-1.0, 1.0)
            samples[i] = (mixed * 19000).toInt().toShort()
        }
        return samples
    }

    private fun generateStarfallSanctuaryMusic(): ShortArray {
        // 8.0 seconds peaceful celestial piano & bell harmony loop
        val durationSec = 8.0f
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val samples = ShortArray(numSamples)

        val chords = listOf(
            doubleArrayOf(164.81, 196.0, 246.94, 329.63), // Em
            doubleArrayOf(130.81, 164.81, 196.0, 246.94), // Cmaj7
            doubleArrayOf(196.0, 246.94, 293.66, 392.0),  // G
            doubleArrayOf(146.83, 220.0, 293.66, 440.0)   // D
        )

        val chordDuration = numSamples / 4
        val chimeNotes = doubleArrayOf(659.25, 783.99, 987.77, 1174.66, 1318.51) // E5, G5, B5, D6, E6

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val chordIndex = (i / chordDuration) % 4
            val timeInChord = (i % chordDuration).toDouble() / SAMPLE_RATE

            // Warm electric piano attack + decay
            val pianoDecay = exp(-timeInChord * 1.8)
            val notes = chords[chordIndex]
            var pianoWave = 0.0
            for (f in notes) {
                pianoWave += sin(2.0 * PI * f * t) + 0.25 * sin(4.0 * PI * f * t)
            }
            pianoWave = (pianoWave / notes.size) * pianoDecay * 0.42

            // High twinkling celestial bell chime every 0.5s
            val chimeStep = (t / 0.5).toInt()
            val timeInChime = t - (chimeStep * 0.5)
            val chimeFreq = chimeNotes[(chimeStep * 3) % chimeNotes.size]
            val chimeDecay = exp(-timeInChime * 10.0)
            val chimeWave = (sin(2.0 * PI * chimeFreq * t) + 0.3 * sin(2.0 * PI * chimeFreq * 2.76 * t)) * chimeDecay * 0.22

            val mixed = (pianoWave + chimeWave).coerceIn(-1.0, 1.0)
            samples[i] = (mixed * 18500).toInt().toShort()
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

