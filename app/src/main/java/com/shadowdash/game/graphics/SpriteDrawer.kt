package com.shadowdash.game.graphics

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import com.shadowdash.game.entities.Crystal
import com.shadowdash.game.entities.CrystalType
import com.shadowdash.game.entities.Obstacle
import com.shadowdash.game.entities.ObstacleType
import com.shadowdash.game.entities.Particle
import com.shadowdash.game.entities.Player
import com.shadowdash.game.entities.PlayerState
import com.shadowdash.game.entities.PowerUp
import com.shadowdash.game.entities.PowerUpType
import kotlin.math.cos
import kotlin.math.sin

class SpriteDrawer {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val auraPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val eyePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val highlightPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val obstaclePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path = Path()

    init {
        highlightPaint.color = Color.WHITE
        highlightPaint.style = Paint.Style.FILL
    }

    fun drawPlayer(canvas: Canvas, player: Player) {
        val cx = player.x + player.width / 2f
        val cy = player.y + player.height / 2f

        canvas.save()

        // 1. Aura glow
        auraPaint.color = player.skin.auraColor.toInt()
        auraPaint.style = Paint.Style.FILL
        val auraRadius = (player.width.coerceAtLeast(player.height) / 2f) + 14f
        canvas.drawCircle(cx, cy, auraRadius, auraPaint)

        // 2. Shield Bubble
        if (player.hasShield) {
            val shieldPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = 0xFF00E5FF.toInt()
                style = Paint.Style.STROKE
                strokeWidth = 4f
                alpha = 200
            }
            val shieldFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = 0x3300E5FF.toInt()
                style = Paint.Style.FILL
            }
            val shieldRadius = auraRadius + 18f
            canvas.drawCircle(cx, cy, shieldRadius, shieldFillPaint)
            canvas.drawCircle(cx, cy, shieldRadius, shieldPaint)
        }

        // 3. Magnet field ring
        if (player.isMagnetActive) {
            val magnetPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = 0xFFE040FB.toInt()
                style = Paint.Style.STROKE
                strokeWidth = 3f
                alpha = ((sin(player.magnetTimer * 10f) * 0.5f + 0.5f) * 160 + 80).toInt()
            }
            canvas.drawCircle(cx, cy, auraRadius + 8f, magnetPaint)
        }

        // 4. Creature Body
        paint.color = player.skin.bodyColor.toInt()
        paint.style = Paint.Style.FILL

        canvas.translate(cx, cy)
        canvas.scale(player.squashStretchX, player.squashStretchY)

        val halfW = player.width / 2f
        val halfH = player.height / 2f

        if (player.state == PlayerState.SLIDING) {
            // Sliding flattened posture with speed streak
            val slideRect = RectF(-halfW, -halfH, halfW, halfH)
            canvas.drawRoundRect(slideRect, 20f, 20f, paint)

            // Speed lines behind sliding player
            val streakPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = player.skin.auraColor.toInt()
                strokeWidth = 4f
            }
            canvas.drawLine(-halfW - 25f, -halfH * 0.3f, -halfW - 5f, -halfH * 0.3f, streakPaint)
            canvas.drawLine(-halfW - 35f, halfH * 0.3f, -halfW - 10f, halfH * 0.3f, streakPaint)

            // Ears tilted back
            path.reset()
            path.moveTo(-halfW * 0.4f, -halfH)
            path.lineTo(-halfW * 0.9f, -halfH - 12f)
            path.lineTo(-halfW * 0.1f, -halfH)
            path.close()
            canvas.drawPath(path, paint)

            // Eyes facing forward
            drawEyes(canvas, eyeX = halfW * 0.35f, eyeY = -halfH * 0.1f, player = player, isSliding = true)
        } else {
            // Normal / Jumping posture: rounded cute shadow body
            val bodyRect = RectF(-halfW, -halfH + 8f, halfW, halfH)
            canvas.drawRoundRect(bodyRect, halfW, halfH, paint)

            // Cute pointy ears / horns
            path.reset()
            // Left ear
            path.moveTo(-halfW * 0.65f, -halfH + 16f)
            path.lineTo(-halfW * 0.7f, -halfH - 18f)
            path.lineTo(-halfW * 0.15f, -halfH + 10f)
            path.close()
            // Right ear
            path.moveTo(halfW * 0.15f, -halfH + 10f)
            path.lineTo(halfW * 0.7f, -halfH - 18f)
            path.lineTo(halfW * 0.65f, -halfH + 16f)
            path.close()
            canvas.drawPath(path, paint)

            // Running feet oscillation
            if (player.state == PlayerState.RUNNING) {
                val legSwing = sin(player.runCycle) * 14f
                val footRadius = 8f
                // Front foot
                canvas.drawCircle(legSwing + 6f, halfH - 2f, footRadius, paint)
                // Back foot
                canvas.drawCircle(-legSwing - 6f, halfH - 2f, footRadius, paint)
            }

            // Big expressive eyes
            drawEyes(canvas, eyeX = 10f, eyeY = -6f, player = player, isSliding = false)
        }

        canvas.restore()
    }

    private fun drawEyes(canvas: Canvas, eyeX: Float, eyeY: Float, player: Player, isSliding: Boolean) {
        eyePaint.color = player.skin.eyeColor.toInt()
        eyePaint.style = Paint.Style.FILL

        if (player.isBlinking) {
            // Closed eye line
            val blinkPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = player.skin.eyeColor.toInt()
                strokeWidth = 3f
            }
            canvas.drawLine(eyeX - 16f, eyeY, eyeX - 4f, eyeY, blinkPaint)
            canvas.drawLine(eyeX + 4f, eyeY, eyeX + 16f, eyeY, blinkPaint)
            return
        }

        val eyeWidth = if (isSliding) 6f else 8f
        val eyeHeight = if (isSliding) 5f else 11f

        // Left eye
        canvas.drawOval(
            eyeX - 18f, eyeY - eyeHeight,
            eyeX - 18f + eyeWidth * 2f, eyeY + eyeHeight,
            eyePaint
        )
        // Right eye
        canvas.drawOval(
            eyeX + 4f, eyeY - eyeHeight,
            eyeX + 4f + eyeWidth * 2f, eyeY + eyeHeight,
            eyePaint
        )

        // Specular glint highlight
        canvas.drawCircle(eyeX - 12f, eyeY - eyeHeight * 0.4f, 3f, highlightPaint)
        canvas.drawCircle(eyeX + 10f, eyeY - eyeHeight * 0.4f, 3f, highlightPaint)
    }

    fun drawObstacle(canvas: Canvas, obs: Obstacle) {
        when (obs.type) {
            ObstacleType.ROCK -> {
                // Rugged ground boulder
                obstaclePaint.color = 0xFF1E293B.toInt()
                val r = RectF(obs.x, obs.y + 10f, obs.x + obs.width, obs.y + obs.height)
                canvas.drawRoundRect(r, 26f, 26f, obstaclePaint)

                // Boulder moss & shadow accent
                val mossPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = 0xFF0D9488.toInt()
                    strokeWidth = 4f
                    style = Paint.Style.STROKE
                }
                canvas.drawArc(r, 190f, 120f, false, mossPaint)
            }
            ObstacleType.SPIKES -> {
                // Jagged sharp purple crystal cluster
                val spikePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = 0xFF7C3AED.toInt()
                    style = Paint.Style.FILL
                }
                val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = 0x88C084FC.toInt()
                    style = Paint.Style.STROKE
                    strokeWidth = 3f
                }
                path.reset()
                path.moveTo(obs.x, obs.y + obs.height)
                path.lineTo(obs.x + 18f, obs.y + 12f)
                path.lineTo(obs.x + 36f, obs.y + obs.height)
                path.lineTo(obs.x + 50f, obs.y)
                path.lineTo(obs.x + 66f, obs.y + obs.height)
                path.lineTo(obs.x + 78f, obs.y + 20f)
                path.lineTo(obs.x + obs.width, obs.y + obs.height)
                path.close()
                canvas.drawPath(path, spikePaint)
                canvas.drawPath(path, glowPaint)
            }
            ObstacleType.LOW_BRANCH -> {
                // Gnarled hanging tree branch with bioluminescent fungus
                val branchPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = 0xFF1E1B18.toInt()
                    style = Paint.Style.FILL
                }
                val r = RectF(obs.x + 15f, obs.y, obs.x + obs.width - 15f, obs.y + obs.height)
                canvas.drawRoundRect(r, 20f, 20f, branchPaint)

                // Glowing turquoise mushrooms hanging under branch
                val fungusPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = 0xFF00E5FF.toInt()
                    style = Paint.Style.FILL
                }
                val bottomY = obs.y + obs.height
                canvas.drawCircle(obs.x + 35f, bottomY - 6f, 9f, fungusPaint)
                canvas.drawCircle(obs.x + obs.width - 35f, bottomY - 4f, 11f, fungusPaint)

                // Warning glow pulse
                val warnPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = 0x5500E5FF.toInt()
                    strokeWidth = 2f
                    style = Paint.Style.STROKE
                }
                canvas.drawCircle(obs.x + 35f, bottomY - 6f, 16f, warnPaint)
            }
            ObstacleType.FLOATING_ORB -> {
                val cx = obs.x + obs.width / 2f
                val cy = obs.y + obs.height / 2f
                val orbGlow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = 0x66E11D48.toInt()
                    style = Paint.Style.FILL
                }
                val orbCore = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = 0xFF9F1239.toInt()
                    style = Paint.Style.FILL
                }
                canvas.drawCircle(cx, cy, obs.width / 2f + 8f, orbGlow)
                canvas.drawCircle(cx, cy, obs.width / 2f, orbCore)
            }
        }
    }

    fun drawCrystal(canvas: Canvas, crystal: Crystal) {
        if (crystal.isCollected) return

        val cx = crystal.x + crystal.size / 2f
        val cy = crystal.y + crystal.size / 2f
        val radius = crystal.size / 2f

        // Outer glow
        val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = crystal.type.color.toInt()
            alpha = 75
            style = Paint.Style.FILL
        }
        canvas.drawCircle(cx, cy, radius + 10f, glowPaint)

        // Diamond gem facet
        val gemPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = crystal.type.color.toInt()
            style = Paint.Style.FILL
        }
        path.reset()
        path.moveTo(cx, cy - radius)
        path.lineTo(cx + radius, cy)
        path.lineTo(cx, cy + radius)
        path.lineTo(cx - radius, cy)
        path.close()
        canvas.drawPath(path, gemPaint)

        // Gem inner facet highlight
        val facetPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            alpha = 150
            style = Paint.Style.FILL
        }
        path.reset()
        path.moveTo(cx, cy - radius)
        path.lineTo(cx + radius * 0.4f, cy)
        path.lineTo(cx, cy - radius * 0.2f)
        path.close()
        canvas.drawPath(path, facetPaint)
    }

    fun drawPowerUp(canvas: Canvas, powerUp: PowerUp) {
        if (powerUp.isCollected) return

        val cx = powerUp.x + powerUp.size / 2f
        val cy = powerUp.y + powerUp.size / 2f
        val radius = powerUp.size / 2f

        val color = if (powerUp.type == PowerUpType.SHIELD) 0xFF00E5FF.toInt() else 0xFFE040FB.toInt()

        // Outer pulse circle
        val pulsePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            alpha = 80
            style = Paint.Style.FILL
        }
        canvas.drawCircle(cx, cy, radius + 12f, pulsePaint)

        // Main sphere
        val mainPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = 0xFF0F172A.toInt()
            style = Paint.Style.FILL
        }
        canvas.drawCircle(cx, cy, radius, mainPaint)

        val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            style = Paint.Style.STROKE
            strokeWidth = 3f
        }
        canvas.drawCircle(cx, cy, radius, strokePaint)

        // Icon inside power-up
        if (powerUp.type == PowerUpType.SHIELD) {
            // Draw shield crest
            val iconPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                this.color = color
                style = Paint.Style.FILL
            }
            path.reset()
            path.moveTo(cx, cy - 10f)
            path.lineTo(cx + 9f, cy - 6f)
            path.lineTo(cx + 7f, cy + 5f)
            path.lineTo(cx, cy + 11f)
            path.lineTo(cx - 7f, cy + 5f)
            path.lineTo(cx - 9f, cy - 6f)
            path.close()
            canvas.drawPath(path, iconPaint)
        } else {
            // Magnet U shape
            val iconPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                this.color = color
                style = Paint.Style.STROKE
                strokeWidth = 4f
                strokeCap = Paint.Cap.ROUND
            }
            val rect = RectF(cx - 8f, cy - 8f, cx + 8f, cy + 8f)
            canvas.drawArc(rect, 0f, 180f, false, iconPaint)
            canvas.drawLine(cx - 8f, cy - 8f, cx - 8f, cy, iconPaint)
            canvas.drawLine(cx + 8f, cy - 8f, cx + 8f, cy, iconPaint)
        }
    }

    fun drawParticle(canvas: Canvas, particle: Particle) {
        val pPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = particle.color
            alpha = (particle.alphaRatio * 255).toInt()
            style = Paint.Style.FILL
        }
        canvas.drawCircle(particle.x, particle.y, particle.size, pPaint)
    }
}

