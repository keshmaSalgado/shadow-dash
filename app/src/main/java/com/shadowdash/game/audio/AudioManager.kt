package com.shadowdash.game.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.net.Uri
import android.util.Log
import com.shadowdash.game.data.GamePreferences
import java.io.File
import java.util.concurrent.Executors

class AudioManager private constructor(private val context: Context) {
    private val preferences = GamePreferences.getInstance(context)
    private var soundPool: SoundPool? = null
    private val soundIds = mutableMapOf<String, Int>()
    private var mediaPlayer: MediaPlayer? = null
    private val executor = Executors.newSingleThreadExecutor()
    private var isInitialized = false

    companion object {
        private const val TAG = "AudioManager"

        const val SOUND_JUMP = "jump"
        const val SOUND_SLIDE = "slide"
        const val SOUND_CRYSTAL_BLUE = "crystal_blue"
        const val SOUND_CRYSTAL_PURPLE = "crystal_purple"
        const val SOUND_POWERUP_SHIELD = "powerup_shield"
        const val SOUND_POWERUP_MAGNET = "powerup_magnet"
        const val SOUND_SHIELD_BREAK = "shield_break"
        const val SOUND_COLLISION = "collision"
        const val SOUND_CLICK = "click"

        @Volatile
        private var instance: AudioManager? = null

        fun getInstance(context: Context): AudioManager {
            return instance ?: synchronized(this) {
                instance ?: AudioManager(context.applicationContext).also { instance = it }
            }
        }
    }

    init {
        initAudio()
    }

    private fun initAudio() {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(8)
            .setAudioAttributes(audioAttributes)
            .build()

        executor.execute {
            try {
                val soundFiles = SoundGenerator.prepareAudioFiles(context)
                soundPool?.let { pool ->
                    for ((key, file) in soundFiles) {
                        if (key != "ambient_music" && file.exists()) {
                            val id = pool.load(file.absolutePath, 1)
                            soundIds[key] = id
                        }
                    }
                }

                // Prepare music player
                val musicFile = soundFiles["ambient_music"]
                if (musicFile != null && musicFile.exists()) {
                    prepareMusicPlayer(musicFile)
                }
                isInitialized = true
            } catch (e: Exception) {
                Log.e(TAG, "Error initializing procedural audio", e)
            }
        }
    }

    private fun prepareMusicPlayer(file: File) {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                setDataSource(context, Uri.fromFile(file))
                isLooping = true
                setVolume(0.55f, 0.55f)
                prepare()
            }
            if (preferences.isMusicEnabled) {
                mediaPlayer?.start()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing background music player", e)
        }
    }

    fun playSound(name: String, volume: Float = 1.0f) {
        if (!preferences.isSoundEnabled || !isInitialized) return
        val id = soundIds[name] ?: return
        try {
            soundPool?.play(id, volume, volume, 1, 0, 1.0f)
        } catch (e: Exception) {
            Log.e(TAG, "Error playing sound: $name", e)
        }
    }

    fun startMusic() {
        if (preferences.isMusicEnabled) {
            try {
                if (mediaPlayer == null) {
                    val musicFile = File(File(context.cacheDir, "shadow_audio"), "ambient_forest.wav")
                    if (musicFile.exists()) {
                        prepareMusicPlayer(musicFile)
                    }
                } else if (mediaPlayer?.isPlaying == false) {
                    mediaPlayer?.start()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error starting music", e)
            }
        }
    }

    fun pauseMusic() {
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error pausing music", e)
        }
    }

    fun resumeMusic() {
        if (preferences.isMusicEnabled) {
            try {
                if (mediaPlayer?.isPlaying == false) {
                    mediaPlayer?.start()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error resuming music", e)
            }
        }
    }

    fun setMusicEnabled(enabled: Boolean) {
        preferences.isMusicEnabled = enabled
        if (enabled) {
            startMusic()
        } else {
            pauseMusic()
        }
    }

    fun setSoundEnabled(enabled: Boolean) {
        preferences.isSoundEnabled = enabled
    }

    fun release() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
            soundPool?.release()
            soundPool = null
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing audio resources", e)
        }
    }
}

