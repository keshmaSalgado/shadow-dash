package com.shadowdash.game

import android.app.Application
import com.shadowdash.game.audio.AudioManager
import com.shadowdash.game.data.GamePreferences

class ShadowDashApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize preferences and preload procedural audio assets
        GamePreferences.getInstance(this)
        AudioManager.getInstance(this)
    }
}

