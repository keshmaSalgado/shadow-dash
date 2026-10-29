package com.shadowdash.game.entities

import com.shadowdash.game.engine.GameRect
import kotlin.math.sin

enum class PowerUpType {
    SHIELD,
    MAGNET
}

class PowerUp(
    var x: Float,
    var baseY: Float,
    val type: PowerUpType
) {
    val size: Float = 44f
    var y: Float = baseY
    var bobPhase: Float = (Math.random() * 6.28).toFloat()
    var isCollected: Boolean = false
    val hitbox: GameRect = GameRect()

    init {
        updateHitbox()
    }

    fun update(dt: Float, speed: Float) {
        if (isCollected) return

        x -= speed * dt
        bobPhase += 3.5f * dt
        y = baseY + sin(bobPhase) * 10f

        updateHitbox()
    }

    private fun updateHitbox() {
        hitbox.set(x, y, x + size, y + size)
    }

    val isOffScreen: Boolean
        get() = x + size < -60f
}

