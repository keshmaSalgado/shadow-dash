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

            // Custom Headpieces, Horns, and Halos based on skin
            drawSkinOrnaments(canvas, halfW, halfH, player.skin.id)
        }

        canvas.restore()
    }

    private fun drawSkinOrnaments(canvas: Canvas, halfW: Float, halfH: Float, skinId: String) {
        val ornamentPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        when (skinId) {
            "lunar_oracle" -> {
                // Silvery crescent halo above head
                ornamentPaint.color = 0xFFE0E7FF.toInt()
                ornamentPaint.style = Paint.Style.STROKE
                ornamentPaint.strokeWidth = 3f
                canvas.drawOval(-halfW * 0.4f, -halfH - 24f, halfW * 0.4f, -halfH - 12f, ornamentPaint)
            }
            "solar_phoenix" -> {
                // Blazing feather crest
                ornamentPaint.color = 0xFFFFD700.toInt()
                ornamentPaint.style = Paint.Style.FILL
                path.reset()
                path.moveTo(0f, -halfH)
                path.lineTo(-6f, -halfH - 22f)
                path.lineTo(0f, -halfH - 14f)
                path.lineTo(6f, -halfH - 22f)
                path.close()
                canvas.drawPath(path, ornamentPaint)
            }
            "emerald_guardian" -> {
                // Forest leaf antlers
                ornamentPaint.color = 0xFF10B981.toInt()
                ornamentPaint.style = Paint.Style.FILL
                canvas.drawOval(-halfW * 0.7f, -halfH - 16f, -halfW * 0.4f, -halfH, ornamentPaint)
                canvas.drawOval(halfW * 0.4f, -halfH - 16f, halfW * 0.7f, -halfH, ornamentPaint)
            }
            "cyber_glitch" -> {
                // Cyberpunk holographic visor strip
                ornamentPaint.color = 0xFF00FF66.toInt()
                ornamentPaint.style = Paint.Style.STROKE
                ornamentPaint.strokeWidth = 2.5f
                canvas.drawLine(-halfW * 0.5f, -halfH * 0.15f, halfW * 0.75f, -halfH * 0.15f, ornamentPaint)
            }
            "frost_specter" -> {
                // Sharp glacial ice spikes
                ornamentPaint.color = 0xFFBAE6FD.toInt()
                ornamentPaint.style = Paint.Style.FILL
                path.reset()
                path.moveTo(-halfW * 0.3f, -halfH + 4f)
                path.lineTo(-halfW * 0.35f, -halfH - 16f)
                path.lineTo(-halfW * 0.15f, -halfH)
                path.close()
                canvas.drawPath(path, ornamentPaint)
            }
            "celestial_dragon" -> {
                // Swept-back golden astral dragon horns
                ornamentPaint.color = 0xFFFDE047.toInt()
                ornamentPaint.style = Paint.Style.FILL
                path.reset()
                path.moveTo(-halfW * 0.2f, -halfH + 6f)
                path.lineTo(-halfW * 0.85f, -halfH - 24f)
                path.lineTo(-halfW * 0.1f, -halfH - 2f)
                path.close()
                canvas.drawPath(path, ornamentPaint)
                path.reset()
                path.moveTo(halfW * 0.2f, -halfH + 6f)
                path.lineTo(halfW * 0.85f, -halfH - 24f)
                path.lineTo(halfW * 0.1f, -halfH - 2f)
                path.close()
                canvas.drawPath(path, ornamentPaint)
            }
        }
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

        val px = particle.x
        val py = particle.y
        val s = particle.size

        when (particle.type) {
            com.shadowdash.game.data.ParticleType.CYBER_PIXELS -> {
                // Square digital data pixel
                canvas.drawRect(px - s, py - s, px + s, py + s, pPaint)
            }
            com.shadowdash.game.data.ParticleType.STARLIGHT_DUST -> {
                // 4-point diamond star
                path.reset()
                path.moveTo(px, py - s * 1.5f)
                path.lineTo(px + s * 0.7f, py)
                path.lineTo(px, py + s * 1.5f)
                path.lineTo(px - s * 0.7f, py)
                path.close()
                canvas.drawPath(path, pPaint)
            }
            com.shadowdash.game.data.ParticleType.FROST_CRYSTALS -> {
                // Ice crystal shard cross
                pPaint.strokeWidth = 2f
                pPaint.style = Paint.Style.STROKE
                canvas.drawLine(px - s, py, px + s, py, pPaint)
                canvas.drawLine(px, py - s, px, py + s, pPaint)
            }
            com.shadowdash.game.data.ParticleType.COSMIC_RUNES -> {
                // Glowing astral rune ring
                pPaint.style = Paint.Style.STROKE
                pPaint.strokeWidth = 2.5f
                canvas.drawCircle(px, py, s, pPaint)
            }
            else -> {
                canvas.drawCircle(px, py, s, pPaint)
            }
        }
    }
}

