package com.shadowdash.game.engine

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import com.shadowdash.game.audio.AudioManager
import com.shadowdash.game.data.CharacterSkin
import com.shadowdash.game.data.GamePreferences
import com.shadowdash.game.data.ParticleType
import com.shadowdash.game.entities.Crystal
import com.shadowdash.game.entities.CrystalType
import com.shadowdash.game.entities.Obstacle
import com.shadowdash.game.entities.Particle
import com.shadowdash.game.entities.Player
import com.shadowdash.game.entities.PlayerState
import com.shadowdash.game.entities.PowerUp
import com.shadowdash.game.entities.PowerUpType
import com.shadowdash.game.graphics.ParallaxBackground
import com.shadowdash.game.graphics.SpriteDrawer

class GameEngine(
    private val context: Context,
    val virtualWidth: Float = 1920f,
    val virtualHeight: Float = 1080f
) {
    val groundY = 880f
    private val preferences = GamePreferences.getInstance(context)
    private val audioManager = AudioManager.getInstance(context)

    // Subsystems
    private val parallax = ParallaxBackground(virtualWidth, virtualHeight, groundY)
    private val spriteDrawer = SpriteDrawer()
    private val spawner = Spawner(virtualWidth, groundY)

    // Game Entities
    val player: Player
    val obstacles = mutableListOf<Obstacle>()
    val crystals = mutableListOf<Crystal>()
    val powerUps = mutableListOf<PowerUp>()
    val particles = mutableListOf<Particle>()

    // Game State
    var gameMode: GameMode = GameMode.COUNTDOWN
        private set

    val stats = RunStats()

    private var baseSpeed = 540f
    var currentSpeed = baseSpeed
        private set

    private var trailTimer = 0f
    private var invulnerableTimer = 0f

    // Callback for UI updates
    var onStateChanged: ((GameMode, RunStats) -> Unit)? = null

    init {
        val currentSkin = CharacterSkin.getById(preferences.selectedSkinId)
        player = Player(x = 240f, groundY = groundY, skin = currentSkin)
        resetGame()
    }

    fun resetGame() {
        val currentSkin = CharacterSkin.getById(preferences.selectedSkinId)
        player.reset(currentSkin)

        obstacles.clear()
        crystals.clear()
        powerUps.clear()
        particles.clear()
        spawner.reset()

        stats.distance = 0f
        stats.score = 0
        stats.crystalsCollected = 0
        stats.isNewHighScore = false
        stats.countdownRemaining = 3.0f

        currentSpeed = baseSpeed
        trailTimer = 0f
        invulnerableTimer = 0f
        gameMode = GameMode.COUNTDOWN

        notifyState()
    }

    fun onJump() {
        if (gameMode == GameMode.COUNTDOWN) {
            // Allow skipping countdown immediately by tapping
            stats.countdownRemaining = 0f
            gameMode = GameMode.PLAYING
            notifyState()
            return
        }

        if (gameMode == GameMode.PLAYING) {
            if (player.jump()) {
                audioManager.playSound(AudioManager.SOUND_JUMP)
                // Spawn jump dust particles at player feet
                spawnJumpDust()
            }
        }
    }

    fun onSlide() {
        if (gameMode == GameMode.PLAYING) {
            if (player.slide()) {
                audioManager.playSound(AudioManager.SOUND_SLIDE)
                // Spawn slide trail dust
                spawnSlideDust()
            }
        }
    }

    fun pause() {
        if (gameMode == GameMode.PLAYING) {
            gameMode = GameMode.PAUSED
            audioManager.pauseMusic()
            notifyState()
        }
    }

    fun resume() {
        if (gameMode == GameMode.PAUSED) {
            gameMode = GameMode.PLAYING
            audioManager.resumeMusic()
            notifyState()
        }
    }

    fun update(rawDt: Float) {
        val dt = rawDt.coerceIn(0.001f, 0.05f)

        when (gameMode) {
            GameMode.COUNTDOWN -> {
                stats.countdownRemaining -= dt
                if (stats.countdownRemaining <= 0f) {
                    gameMode = GameMode.PLAYING
                    audioManager.startMusic()
                }
                notifyState()
            }
            GameMode.PLAYING -> {
                updatePlaying(dt)
            }
            GameMode.PAUSED, GameMode.GAME_OVER -> {
                // Update particles only for game-over lingering effects
                updateParticles(dt)
            }
        }
    }

    private fun updatePlaying(dt: Float) {
        // Difficulty scaling: speed increases with distance
        val speedBonus = (stats.distance / 100f) * 6.5f
        currentSpeed = (baseSpeed + speedBonus).coerceAtMost(980f)

        // Distance & Score
        val distanceGained = currentSpeed * dt * 0.08f
        stats.distance += distanceGained
        stats.score = stats.distance.toInt() + (stats.crystalsCollected * 10)

        // Update Parallax
        parallax.update(dt, currentSpeed)

        // Update Player
        player.update(dt, currentSpeed)

        // Invulnerability
        if (invulnerableTimer > 0f) {
            invulnerableTimer -= dt
        }

        // Emit Player Trail Particles
        trailTimer += dt
        if (trailTimer > 0.045f) {
            trailTimer = 0f
            spawnPlayerTrail()
        }

        // Update Spawner
        spawner.update(dt, currentSpeed, obstacles, crystals, powerUps)

        // Update Obstacles
        val obsIterator = obstacles.iterator()
        while (obsIterator.hasNext()) {
            val obs = obsIterator.next()
            obs.update(dt, currentSpeed)

            if (obs.isOffScreen) {
                obsIterator.remove()
                continue
            }

            // Check collision with player
            if (invulnerableTimer <= 0f && CollisionDetector.checkObstacleCollision(player, obs)) {
                if (player.hasShield) {
                    // Shield absorbs collision
                    player.hasShield = false
                    invulnerableTimer = 1.0f // 1 second grace period
                    audioManager.playSound(AudioManager.SOUND_SHIELD_BREAK)
                    spawnShieldBreakParticles(player.x + player.width / 2f, player.y + player.height / 2f)
                    obsIterator.remove()
                } else {
                    // Fatal collision
                    handleGameOver()
                    return
                }
            }
        }

        // Update Crystals
        val crystalIterator = crystals.iterator()
        while (crystalIterator.hasNext()) {
            val c = crystalIterator.next()
            c.update(dt, currentSpeed, player.x + player.width / 2f, player.y + player.height / 2f, player.isMagnetActive)

            if (c.isOffScreen) {
                crystalIterator.remove()
                continue
            }

            if (CollisionDetector.checkCrystalCollision(player, c)) {
                c.isCollected = true
                stats.crystalsCollected += c.type.points
                stats.score += c.type.points * 15

                val sound = if (c.type == CrystalType.PURPLE) AudioManager.SOUND_CRYSTAL_PURPLE else AudioManager.SOUND_CRYSTAL_BLUE
                audioManager.playSound(sound)
                spawnCrystalSparkles(c.x + c.size / 2f, c.y + c.size / 2f, c.type.color.toInt())
                crystalIterator.remove()
            }
        }

        // Update PowerUps
        val powerUpIterator = powerUps.iterator()
        while (powerUpIterator.hasNext()) {
            val p = powerUpIterator.next()
            p.update(dt, currentSpeed)

            if (p.isOffScreen) {
                powerUpIterator.remove()
                continue
            }

            if (CollisionDetector.checkPowerUpCollision(player, p)) {
                p.isCollected = true
                if (p.type == PowerUpType.SHIELD) {
                    player.hasShield = true
                    audioManager.playSound(AudioManager.SOUND_POWERUP_SHIELD)
                    spawnSparkleBurst(p.x, p.y, 0xFF00E5FF.toInt(), 20)
                } else {
                    player.magnetTimer = 12.0f // 12 seconds magnet active
                    audioManager.playSound(AudioManager.SOUND_POWERUP_MAGNET)
                    spawnSparkleBurst(p.x, p.y, 0xFFE040FB.toInt(), 20)
                }
                powerUpIterator.remove()
            }
        }

        // Update Particles
        updateParticles(dt)

        notifyState()
    }

    private fun updateParticles(dt: Float) {
        val pIterator = particles.iterator()
        while (pIterator.hasNext()) {
            val p = pIterator.next()
            p.update(dt)
            if (p.isDead) {
                pIterator.remove()
            }
        }
    }

    private fun handleGameOver() {
        player.setDead()
        gameMode = GameMode.GAME_OVER
        audioManager.playSound(AudioManager.SOUND_COLLISION)

        // Save progress securely
        preferences.addCrystals(stats.crystalsCollected)
        val isNewHigh = preferences.updateHighScore(stats.score)
        stats.isNewHighScore = isNewHigh

        spawnDeathBurst(player.x + player.width / 2f, player.y + player.height / 2f)
        notifyState()
    }

    private fun spawnPlayerTrail() {
        val px = player.x + 8f
        val py = player.y + player.height * 0.65f
        val color = player.skin.auraColor.toInt()
        particles.add(
            Particle(
                x = px,
                y = py,
                vx = -currentSpeed * 0.25f + (Math.random().toFloat() - 0.5f) * 50f,
                vy = (Math.random().toFloat() - 0.5f) * 35f,
                size = (Math.random() * 5f + 3f).toFloat(),
                color = color,
                maxLife = 0.45f,
                type = player.skin.particleType
            )
        )
    }

    private fun spawnJumpDust() {
        val px = player.x + player.width / 2f
        val py = groundY
        for (i in 0..7) {
            particles.add(
                Particle(
                    x = px + (Math.random().toFloat() - 0.5f) * 30f,
                    y = py,
                    vx = (Math.random().toFloat() - 0.5f) * 120f,
                    vy = -Math.random().toFloat() * 70f,
                    size = (Math.random() * 4f + 3f).toFloat(),
                    color = 0xAA64748B.toInt(),
                    maxLife = 0.4f
                )
            )
        }
    }

    private fun spawnSlideDust() {
        val px = player.x
        val py = groundY
        for (i in 0..5) {
            particles.add(
                Particle(
                    x = px,
                    y = py,
                    vx = -currentSpeed * 0.4f,
                    vy = -Math.random().toFloat() * 40f,
                    size = (Math.random() * 5f + 2f).toFloat(),
                    color = 0x8894A3B8.toInt(),
                    maxLife = 0.35f
                )
            )
        }
    }

    private fun spawnCrystalSparkles(x: Float, y: Float, color: Int) {
        for (i in 0..12) {
            val angle = Math.random() * 6.28
            val spd = (Math.random() * 200 + 80).toFloat()
            particles.add(
                Particle(
                    x = x,
                    y = y,
                    vx = (Math.cos(angle) * spd).toFloat(),
                    vy = (Math.sin(angle) * spd).toFloat(),
                    size = (Math.random() * 4f + 2f).toFloat(),
                    color = color,
                    maxLife = 0.5f,
                    type = ParticleType.CYAN_SPARKS
                )
            )
        }
    }

    private fun spawnShieldBreakParticles(x: Float, y: Float) {
        spawnSparkleBurst(x, y, 0xFF00E5FF.toInt(), 25)
    }

    private fun spawnDeathBurst(x: Float, y: Float) {
        spawnSparkleBurst(x, y, player.skin.eyeColor.toInt(), 30)
    }

    private fun spawnSparkleBurst(x: Float, y: Float, color: Int, count: Int) {
        for (i in 0 until count) {
            val angle = Math.random() * 6.28
            val spd = (Math.random() * 260 + 100).toFloat()
            particles.add(
                Particle(
                    x = x,
                    y = y,
                    vx = (Math.cos(angle) * spd).toFloat(),
                    vy = (Math.sin(angle) * spd).toFloat(),
                    size = (Math.random() * 6f + 3f).toFloat(),
                    color = color,
                    maxLife = 0.65f,
                    type = ParticleType.SHADOW_SMOKE
                )
            )
        }
    }

    fun draw(canvas: Canvas) {
        // 1. Parallax Layers
        parallax.draw(canvas)

        // 2. Obstacles
        for (obs in obstacles) {
            spriteDrawer.drawObstacle(canvas, obs)
        }

        // 3. Crystals
        for (c in crystals) {
            spriteDrawer.drawCrystal(canvas, c)
        }

        // 4. Power-ups
        for (p in powerUps) {
            spriteDrawer.drawPowerUp(canvas, p)
        }

        // 5. Particles
        for (pt in particles) {
            spriteDrawer.drawParticle(canvas, pt)
        }

        // 6. Player
        spriteDrawer.drawPlayer(canvas, player)
    }

    private fun notifyState() {
        onStateChanged?.invoke(gameMode, stats)
    }
}

