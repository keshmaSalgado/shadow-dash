package com.shadowdash.game.entities

import com.shadowdash.game.engine.GameRect

enum class ObstacleType {
    ROCK,
    SPIKES,
    LOW_BRANCH,
    FLOATING_ORB
}

class Obstacle(
    var x: Float,
    val type: ObstacleType,
    val groundY: Float
) {
    val width: Float
    val height: Float
    val y: Float
    val hitbox: GameRect = GameRect()

    init {
        when (type) {
            ObstacleType.ROCK -> {
                width = 75f
                height = 70f
                y = groundY - height
            }
            ObstacleType.SPIKES -> {
                width = 85f
                height = 55f
                y = groundY - height
            }
            ObstacleType.LOW_BRANCH -> {
                // High branch descending down; leaves clear sliding space of ~65px above ground
                width = 110f
                height = 200f
                // Top comes from above, bottom ends 62px above ground
                y = groundY - 62f - height
            }
            ObstacleType.FLOATING_ORB -> {
                width = 65f
                height = 65f
                y = groundY - 145f // floats around jump/head level
            }
        }
        updateHitbox()
    }

    fun update(dt: Float, speed: Float) {
        x -= speed * dt
        updateHitbox()
    }

    private fun updateHitbox() {
        val margin = 8f
        hitbox.set(
            x + margin,
            y + margin,
            x + width - margin,
            y + height - margin
        )
    }

    val isOffScreen: Boolean
        get() = x + width < -100f
}

