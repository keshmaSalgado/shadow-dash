package com.shadowdash.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.shadowdash.game.audio.AudioManager
import com.shadowdash.game.data.GamePreferences
import com.shadowdash.game.ui.CharacterSelectScreen
import com.shadowdash.game.ui.GameScreen
import com.shadowdash.game.ui.MainMenuScreen
import com.shadowdash.game.ui.ScreenState
import com.shadowdash.game.ui.SettingsScreen
import com.shadowdash.game.ui.SplashScreen
import com.shadowdash.game.ui.theme.MidnightDark
import com.shadowdash.game.ui.theme.ShadowDashTheme

class MainActivity : ComponentActivity() {

    private lateinit var preferences: GamePreferences
    private lateinit var audioManager: AudioManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup sticky immersive full screen for responsive gameplay
        setupImmersiveMode()

        preferences = GamePreferences.getInstance(this)
        audioManager = AudioManager.getInstance(this)

        setContent {
            ShadowDashTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MidnightDark
                ) {
                    ShadowDashRootNavigation(
                        preferences = preferences,
                        audioManager = audioManager
                    )
                }
            }
        }
    }

    private fun setupImmersiveMode() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            setupImmersiveMode()
        }
    }

    override fun onResume() {
        super.onResume()
        audioManager.resumeMusic()
    }

    override fun onPause() {
        super.onPause()
        audioManager.pauseMusic()
    }

    override fun onDestroy() {
        super.onDestroy()
        audioManager.release()
    }
}

@Composable
fun ShadowDashRootNavigation(
    preferences: GamePreferences,
    audioManager: AudioManager
) {
    var currentScreen by remember { mutableStateOf(ScreenState.SPLASH) }

    Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
        when (screen) {
            ScreenState.SPLASH -> {
                SplashScreen(
                    onSplashComplete = {
                        currentScreen = ScreenState.MAIN_MENU
                        audioManager.startMusic()
                    }
                )
            }
            ScreenState.MAIN_MENU -> {
                MainMenuScreen(
                    preferences = preferences,
                    onPlayClick = {
                        audioManager.playSound(AudioManager.SOUND_CLICK)
                        currentScreen = ScreenState.GAME
                    },
                    onCharactersClick = {
                        audioManager.playSound(AudioManager.SOUND_CLICK)
                        currentScreen = ScreenState.CHARACTERS
                    },
                    onSettingsClick = {
                        audioManager.playSound(AudioManager.SOUND_CLICK)
                        currentScreen = ScreenState.SETTINGS
                    }
                )
            }
            ScreenState.GAME -> {
                GameScreen(
                    onNavigateToMenu = {
                        currentScreen = ScreenState.MAIN_MENU
                    }
                )
            }
            ScreenState.CHARACTERS -> {
                CharacterSelectScreen(
                    preferences = preferences,
                    onBackClick = {
                        audioManager.playSound(AudioManager.SOUND_CLICK)
                        currentScreen = ScreenState.MAIN_MENU
                    }
                )
            }
            ScreenState.SETTINGS -> {
                SettingsScreen(
                    preferences = preferences,
                    audioManager = audioManager,
                    onBackClick = {
                        audioManager.playSound(AudioManager.SOUND_CLICK)
                        currentScreen = ScreenState.MAIN_MENU
                    }
                )
            }
        }
    }
}

