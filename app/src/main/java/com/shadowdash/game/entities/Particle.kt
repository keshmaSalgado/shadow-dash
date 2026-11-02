package com.shadowdash.game.entities

import android.graphics.Color
import com.shadowdash.game.data.ParticleType

class Particle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var size: Float,
    val color: Int,
    val maxLife: Float,
    val type: ParticleType = ParticleType.SHADOW_SMOKE
) {
    var life: Float = maxLife
    val isDead: Boolean get() = life <= 0f

    fun update(dt: Float) {
        x += vx * dt
        y += vy * dt

        when (type) {
            ParticleType.SHADOW_SMOKE -> {
                vy -= 15f * dt // gently float upward
                vx *= 0.95f
                size *= (1f + 0.3f * dt) // expand
            }
            ParticleType.CYAN_SPARKS -> {
                vy += 40f * dt // slight gravity
                vx += (Math.random().toFloat() - 0.5f) * 60f * dt
            }
            ParticleType.VOID_EMBERS -> {
                vy -= 25f * dt // float upward
                vx += (Math.random().toFloat() - 0.5f) * 30f * dt
            }
            ParticleType.CRIMSON_FLAMES -> {
                vy -= 45f * dt // rise quickly
                size *= (1f - 0.5f * dt)
            }
            ParticleType.STARLIGHT_DUST -> {
                vy -= 10f * dt // gentle levitation
                vx *= 0.92f
                size *= (1f - 0.25f * dt)
            }
            ParticleType.SOLAR_SPARKS -> {
                vy -= 30f * dt // flare upwards with explosive spread
                vx += (Math.random().toFloat() - 0.5f) * 80f * dt
            }
            ParticleType.EMERALD_SPORES -> {
                vy += (Math.sin(life.toDouble() * 10.0).toFloat()) * 30f * dt // sinusoidal wave drift
                vx *= 0.94f
            }
            ParticleType.CYBER_PIXELS -> {
                // Stepped digital glitch movement
                if (Math.random() < 0.1) {
                    vx += (Math.random().toFloat() - 0.5f) * 140f
                    vy += (Math.random().toFloat() - 0.5f) * 140f
                }
            }
            ParticleType.FROST_CRYSTALS -> {
                vy += 20f * dt // slow gentle snowfall drift
                vx += (Math.random().toFloat() - 0.5f) * 25f * dt
            }
            ParticleType.COSMIC_RUNES -> {
                vy -= 18f * dt // astral ascent
                vx *= 0.9f
                size *= (1f + 0.15f * dt)
            }
        }

        life -= dt
    }

    val alphaRatio: Float
        get() = (life / maxLife).coerceIn(0f, 1f)
}

