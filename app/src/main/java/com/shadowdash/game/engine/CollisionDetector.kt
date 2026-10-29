package com.shadowdash.game.engine

import com.shadowdash.game.entities.Crystal
import com.shadowdash.game.entities.Obstacle
import com.shadowdash.game.entities.Player
import com.shadowdash.game.entities.PowerUp

object CollisionDetector {

    fun checkObstacleCollision(player: Player, obstacle: Obstacle): Boolean {
        return player.hitbox.intersects(obstacle.hitbox)
    }

    fun checkCrystalCollision(player: Player, crystal: Crystal): Boolean {
        if (crystal.isCollected) return false
        return player.hitbox.intersects(crystal.hitbox)
    }

    fun checkPowerUpCollision(player: Player, powerUp: PowerUp): Boolean {
        if (powerUp.isCollected) return false
        return player.hitbox.intersects(powerUp.hitbox)
    }
}
