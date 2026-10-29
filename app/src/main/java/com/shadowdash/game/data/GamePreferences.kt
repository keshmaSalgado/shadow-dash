package com.shadowdash.game.data

import android.content.Context
import android.content.SharedPreferences

class GamePreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "shadow_dash_preferences"
        private const val KEY_HIGH_SCORE = "high_score"
        private const val KEY_TOTAL_CRYSTALS = "total_crystals"
        private const val KEY_UNLOCKED_SKINS = "unlocked_skins"
        private const val KEY_SELECTED_SKIN = "selected_skin"
        private const val KEY_SOUND_ENABLED = "sound_enabled"
        private const val KEY_MUSIC_ENABLED = "music_enabled"
        private const val KEY_HAPTICS_ENABLED = "haptics_enabled"

        @Volatile
        private var instance: GamePreferences? = null

        fun getInstance(context: Context): GamePreferences {
            return instance ?: synchronized(this) {
                instance ?: GamePreferences(context.applicationContext).also { instance = it }
            }
        }
    }

    var highScore: Int
        get() = prefs.getInt(KEY_HIGH_SCORE, 0)
        private set(value) = prefs.edit().putInt(KEY_HIGH_SCORE, value).apply()

    var totalCrystals: Int
        get() = prefs.getInt(KEY_TOTAL_CRYSTALS, 0)
        private set(value) = prefs.edit().putInt(KEY_TOTAL_CRYSTALS, value).apply()

    var selectedSkinId: String
        get() = prefs.getString(KEY_SELECTED_SKIN, CharacterSkin.DEFAULT_SKIN_ID) ?: CharacterSkin.DEFAULT_SKIN_ID
        set(value) = prefs.edit().putString(KEY_SELECTED_SKIN, value).apply()

    var isSoundEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOUND_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_SOUND_ENABLED, value).apply()

    var isMusicEnabled: Boolean
        get() = prefs.getBoolean(KEY_MUSIC_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_MUSIC_ENABLED, value).apply()

    var isHapticsEnabled: Boolean
        get() = prefs.getBoolean(KEY_HAPTICS_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_HAPTICS_ENABLED, value).apply()

    fun getUnlockedSkins(): Set<String> {
        val defaultSet = setOf(CharacterSkin.DEFAULT_SKIN_ID)
        return prefs.getStringSet(KEY_UNLOCKED_SKINS, defaultSet)?.toSet() ?: defaultSet
    }

    @Synchronized
    fun updateHighScore(score: Int): Boolean {
        if (score > highScore) {
            highScore = score
            return true
        }
        return false
    }

    @Synchronized
    fun addCrystals(amount: Int) {
        if (amount > 0) {
            totalCrystals += amount
        }
    }

    @Synchronized
    fun unlockSkin(skinId: String, cost: Int): Boolean {
        val currentSkins = getUnlockedSkins().toMutableSet()
        if (currentSkins.contains(skinId)) {
            return true
        }
        if (totalCrystals >= cost) {
            totalCrystals -= cost
            currentSkins.add(skinId)
            prefs.edit().putStringSet(KEY_UNLOCKED_SKINS, currentSkins).apply()
            return true
        }
        return false
    }

    fun isSkinUnlocked(skinId: String): Boolean {
        if (skinId == CharacterSkin.DEFAULT_SKIN_ID) return true
        return getUnlockedSkins().contains(skinId)
    }

    @Synchronized
    fun resetAllData() {
        prefs.edit().clear().apply()
        selectedSkinId = CharacterSkin.DEFAULT_SKIN_ID
    }
}

