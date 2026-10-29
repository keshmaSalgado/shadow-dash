package com.shadowdash.game.entities

import com.shadowdash.game.data.CharacterSkin
import com.shadowdash.game.engine.GameRect
import kotlin.math.sin

enum class PlayerState {
    RUNNING,
    JUMPING,
    FALLING,
    SLIDING,
    DEAD
}

class Player(
    var x: Float,
    var groundY: Float,
    var skin: CharacterSkin
) {
    companion object {
        const val BASE_WIDTH = 85f
        const val BASE_HEIGHT = 95f
        const val SLIDE_WIDTH = 115f
        const val SLIDE_HEIGHT = 48f
        const val JUMP_VELOCITY = -1150f
        const val DOUBLE_JUMP_VELOCITY = -1000f
        const val GRAVITY = 2800f
        const val SLIDE_DURATION = 0.65f
    }

    var y: Float = groundY - BASE_HEIGHT
    var vy: Float = 0f
    var width: Float = BASE_WIDTH
    var height: Float = BASE_HEIGHT

    var state: PlayerState = PlayerState.RUNNING
        private set

    var jumpCount = 0
    private val maxJumps = 2

    private var slideTimer: Float = 0f

    // Power-ups
    var hasShield: Boolean = false
    var magnetTimer: Float = 0f
    val isMagnetActive: Boolean get() = magnetTimer > 0f

    // Visuals & animation
    var runCycle: Float = 0f
    var eyeBlinkTimer: Float = 0f
    var isBlinking: Boolean = false
    var squashStretchX: Float = 1f
    var squashStretchY: Float = 1f

    val hitbox: GameRect = GameRect()

    init {
        updateHitbox()
    }

    fun jump(): Boolean {
        if (state == PlayerState.DEAD) return false

        if (state == PlayerState.SLIDING) {
            cancelSlide()
        }

        if (jumpCount < maxJumps) {
            val vel = if (jumpCount == 0) JUMP_VELOCITY else DOUBLE_JUMP_VELOCITY
            vy = vel
            jumpCount++
            state = PlayerState.JUMPING
            squashStretchX = 0.8f
            squashStretchY = 1.25f
            return true
        }
        return false
    }

    fun slide(): Boolean {
        if (state == PlayerState.DEAD) return false

        // If in air, dive down quickly
        if (state == PlayerState.JUMPING || state == PlayerState.FALLING) {
            vy = 1200f
        }

        state = PlayerState.SLIDING
        slideTimer = SLIDE_DURATION
        width = SLIDE_WIDTH
        height = SLIDE_HEIGHT
        y = groundY - SLIDE_HEIGHT
        squashStretchX = 1.35f
        squashStretchY = 0.65f
        updateHitbox()
        return true
    }

    fun cancelSlide() {
        if (state == PlayerState.SLIDING) {
            state = PlayerState.RUNNING
            slideTimer = 0f
            width = BASE_WIDTH
            height = BASE_HEIGHT
            y = groundY - BASE_HEIGHT
            squashStretchX = 1f
            squashStretchY = 1f
            updateHitbox()
        }
    }

    fun update(dt: Float, gameSpeed: Float) {
        if (state == PlayerState.DEAD) return

        // Update powerups
        if (magnetTimer > 0f) {
            magnetTimer -= dt
            if (magnetTimer < 0f) magnetTimer = 0f
        }

        // Animation cycles
        runCycle += dt * (gameSpeed / 400f) * 16f
        eyeBlinkTimer += dt
        if (eyeBlinkTimer > 3.5f) {
            isBlinking = true
            if (eyeBlinkTimer > 3.65f) {
                isBlinking = false
                eyeBlinkTimer = (Math.random() * 1.5).toFloat()
            }
        }

        // Smooth return of squash and stretch to 1
        squashStretchX += (1f - squashStretchX) * 10f * dt
        squashStretchY += (1f - squashStretchY) * 10f * dt

        // Sliding update
        if (state == PlayerState.SLIDING) {
            slideTimer -= dt
            if (slideTimer <= 0f) {
                cancelSlide()
            }
        }

        // Airborne physics
        if (state == PlayerState.JUMPING || state == PlayerState.FALLING) {
            vy += GRAVITY * dt
            y += vy * dt

            if (vy > 0f) {
                state = PlayerState.FALLING
            }

            // Ground landing
            if (y >= groundY - height) {
                y = groundY - height
                vy = 0f
                jumpCount = 0
                state = PlayerState.RUNNING
                squashStretchX = 1.2f
                squashStretchY = 0.8f
            }
        }

        updateHitbox()
    }

    fun updateHitbox() {
        // Fair hitbox with padding so grazing touches don't punish player
        val marginX = if (state == PlayerState.SLIDING) 8f else 12f
        val marginY = if (state == PlayerState.SLIDING) 6f else 10f
        hitbox.set(
            x + marginX,
            y + marginY,
            x + width - marginX,
            y + height - 2f
        )
    }

    fun setDead() {
        state = PlayerState.DEAD
        vy = -400f
    }

    fun reset(skin: CharacterSkin) {
        this.skin = skin
        state = PlayerState.RUNNING
        width = BASE_WIDTH
        height = BASE_HEIGHT
        y = groundY - BASE_HEIGHT
        vy = 0f
        jumpCount = 0
        slideTimer = 0f
        hasShield = false
        magnetTimer = 0f
        squashStretchX = 1f
        squashStretchY = 1f
        updateHitbox()
    }
}

