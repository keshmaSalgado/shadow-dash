package com.shadowdash.game.engine

import com.shadowdash.game.entities.Crystal
import com.shadowdash.game.entities.CrystalType
import com.shadowdash.game.entities.Obstacle
import com.shadowdash.game.entities.ObstacleType
import com.shadowdash.game.entities.PowerUp
import com.shadowdash.game.entities.PowerUpType

class Spawner(
    private val virtualWidth: Float,
    private val groundY: Float
) {
    private var nextSpawnX: Float = virtualWidth + 300f
    private var distanceSincePowerUp: Float = 0f
    private val powerUpDistanceInterval = 900f

    fun reset() {
        nextSpawnX = virtualWidth + 400f
        distanceSincePowerUp = 0f
    }

    fun update(
        dt: Float,
        speed: Float,
        obstacles: MutableList<Obstacle>,
        crystals: MutableList<Crystal>,
        powerUps: MutableList<PowerUp>
    ) {
        // Move spawn marker towards player at same rate as world moves
        nextSpawnX -= speed * dt
        distanceSincePowerUp += speed * dt

        if (nextSpawnX <= virtualWidth + 100f) {
            spawnPattern(obstacles, crystals, powerUps, speed)
        }
    }

    private fun spawnPattern(
        obstacles: MutableList<Obstacle>,
        crystals: MutableList<Crystal>,
        powerUps: MutableList<PowerUp>,
        speed: Float
    ) {
        val spawnX = virtualWidth + 150f
        val patternType = (Math.random() * 5).toInt()

        when (patternType) {
            0 -> {
                // Ground Rock with crystal jump arc
                obstacles.add(Obstacle(spawnX, ObstacleType.ROCK, groundY))
                crystals.add(Crystal(spawnX - 40f, groundY - 140f, CrystalType.BLUE))
                crystals.add(Crystal(spawnX + 35f, groundY - 190f, CrystalType.BLUE))
                crystals.add(Crystal(spawnX + 110f, groundY - 140f, CrystalType.BLUE))
            }
            1 -> {
                // Low Branch requiring Slide with ground crystals
                obstacles.add(Obstacle(spawnX, ObstacleType.LOW_BRANCH, groundY))
                crystals.add(Crystal(spawnX + 10f, groundY - 35f, CrystalType.BLUE))
                crystals.add(Crystal(spawnX + 60f, groundY - 35f, CrystalType.BLUE))
                crystals.add(Crystal(spawnX + 110f, groundY - 35f, CrystalType.BLUE))
            }
            2 -> {
                // Spikes with rare Purple Crystal reward
                obstacles.add(Obstacle(spawnX, ObstacleType.SPIKES, groundY))
                crystals.add(Crystal(spawnX + 25f, groundY - 180f, CrystalType.PURPLE))
            }
            3 -> {
                // Floating Orb hovering at mid-height
                obstacles.add(Obstacle(spawnX, ObstacleType.FLOATING_ORB, groundY))
                // Ground crystals beneath it
                crystals.add(Crystal(spawnX + 10f, groundY - 40f, CrystalType.BLUE))
                crystals.add(Crystal(spawnX + 70f, groundY - 40f, CrystalType.BLUE))
            }
            4 -> {
                // Slide under Low Branch then Jump over Rock combo
                obstacles.add(Obstacle(spawnX, ObstacleType.LOW_BRANCH, groundY))
                val rockX = spawnX + 280f
                obstacles.add(Obstacle(rockX, ObstacleType.ROCK, groundY))
                crystals.add(Crystal(rockX + 35f, groundY - 180f, CrystalType.PURPLE))
            }
        }

        // Check if power-up should spawn
        if (distanceSincePowerUp >= powerUpDistanceInterval) {
            distanceSincePowerUp = 0f
            val powerType = if (Math.random() > 0.5) PowerUpType.SHIELD else PowerUpType.MAGNET
            val pX = spawnX + 450f
            powerUps.add(PowerUp(pX, groundY - 150f, powerType))
        }

        // Determine spacing to next obstacle cluster based on current game speed
        val gap = (speed * (1.1f + Math.random().toFloat() * 0.7f)).coerceIn(550f, 950f)
        nextSpawnX = spawnX + gap
    }
}

