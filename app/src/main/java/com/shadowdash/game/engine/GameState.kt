package com.shadowdash.game.engine

enum class GameMode {
    COUNTDOWN,
    PLAYING,
    PAUSED,
    GAME_OVER
}

data class RunStats(
    var distance: Float = 0f,
    var score: Int = 0,
    var crystalsCollected: Int = 0,
    var isNewHighScore: Boolean = false,
    var countdownRemaining: Float = 3.0f
)

