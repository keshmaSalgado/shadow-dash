package com.shadowdash.game.entities

import com.shadowdash.game.engine.GameRect
import kotlin.math.hypot
import kotlin.math.sin

enum class CrystalType(val points: Int, val color: Long) {
    BLUE(1, 0xFF00E5FF),
    PURPLE(5, 0xFFE040FB)
}

class Crystal(
    var x: Float,
    var baseY: Float,
    val type: CrystalType
) {
    val size: Float = 36f
    var y: Float = baseY
    var bobPhase: Float = (Math.random() * 6.28).toFloat()
    var isCollected: Boolean = false
    val hitbox: GameRect = GameRect()

    init {
        updateHitbox()
    }

    fun update(dt: Float, speed: Float, playerX: Float, playerY: Float, isMagnetActive: Boolean) {
        if (isCollected) return

        x -= speed * dt
        bobPhase += 4f * dt
        y = baseY + sin(bobPhase) * 8f

        // Magnet attraction
        if (isMagnetActive) {
            val dx = playerX - x
            val dy = playerY - y
            val dist = hypot(dx, dy)
            val magnetRadius = 450f

            if (dist < magnetRadius && dist > 5f) {
                val pullSpeed = 900f * (1f - dist / magnetRadius) + 400f
                x += (dx / dist) * pullSpeed * dt
                y += (dy / dist) * pullSpeed * dt
            }
        }

        updateHitbox()
    }

    private fun updateHitbox() {
        hitbox.set(x, y, x + size, y + size)
    }

    val isOffScreen: Boolean
        get() = x + size < -50f
}

