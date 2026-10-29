package com.shadowdash.game.graphics

import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import kotlin.math.sin

class ParallaxBackground(
    private val virtualWidth: Float,
    private val virtualHeight: Float,
    private val groundY: Float
) {
    private val skyPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val starPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val moonPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val moonGlowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mountainPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val midTreesPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mushroomGlowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val groundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val grassPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val sporePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Layer offsets
    private var mountainOffset = 0f
    private var midTreesOffset = 0f
    private var groundOffset = 0f

    // Star data
    private val stars = List(45) {
        Star(
            x = (Math.random() * virtualWidth).toFloat(),
            y = (Math.random() * (groundY * 0.65f)).toFloat(),
            radius = (Math.random() * 2.2f + 1f).toFloat(),
            twinkleSpeed = (Math.random() * 3f + 1.5f).toFloat(),
            twinklePhase = (Math.random() * 6.28f).toFloat()
        )
    }

    // Drifting spores
    private val spores = List(30) {
        Spore(
            x = (Math.random() * virtualWidth).toFloat(),
            y = (Math.random() * (groundY - 100f) + 100f).toFloat(),
            size = (Math.random() * 3.5f + 1.5f).toFloat(),
            driftSpeed = (Math.random() * 40f + 25f).toFloat(),
            color = if (Math.random() > 0.5) 0xFF00E5FF.toInt() else 0xFFE040FB.toInt()
        )
    }

    private class Star(val x: Float, val y: Float, val radius: Float, val twinkleSpeed: Float, var twinklePhase: Float)
    private class Spore(var x: Float, var y: Float, val size: Float, val driftSpeed: Float, val color: Int)

    init {
        // Sky gradient: very dark midnight blue to deep navy
        skyPaint.shader = LinearGradient(
            0f, 0f, 0f, groundY,
            intArrayOf(0xFF04060E.toInt(), 0xFF0A1226.toInt(), 0xFF141F3C.toInt()),
            floatArrayOf(0f, 0.6f, 1f),
            Shader.TileMode.CLAMP
        )

        starPaint.color = 0xFFE0E7FF.toInt()

        moonGlowPaint.color = 0x3300E5FF.toInt()
        moonGlowPaint.style = Paint.Style.FILL

        moonPaint.color = 0xFFD4E5FF.toInt()
        moonPaint.style = Paint.Style.FILL

        mountainPaint.color = 0xFF0B1224.toInt()
        midTreesPaint.color = 0xFF080D1A.toInt()
        mushroomGlowPaint.style = Paint.Style.FILL

        groundPaint.shader = LinearGradient(
            0f, groundY, 0f, virtualHeight,
            intArrayOf(0xFF0F172A.toInt(), 0xFF060911.toInt()),
            null,
            Shader.TileMode.CLAMP
        )

        grassPaint.color = 0xFF00E5FF.toInt()
        grassPaint.strokeWidth = 3f
        grassPaint.style = Paint.Style.STROKE
    }

    fun update(dt: Float, speed: Float) {
        mountainOffset = (mountainOffset + speed * 0.12f * dt) % virtualWidth
        midTreesOffset = (midTreesOffset + speed * 0.45f * dt) % virtualWidth
        groundOffset = (groundOffset + speed * dt) % 120f

        for (star in stars) {
            star.twinklePhase += star.twinkleSpeed * dt
        }

        for (spore in spores) {
            spore.x -= (spore.driftSpeed + speed * 0.3f) * dt
            spore.y += sin(spore.x * 0.02f) * 12f * dt
            if (spore.x < -20f) {
                spore.x = virtualWidth + 20f
                spore.y = (Math.random() * (groundY - 100f) + 100f).toFloat()
            }
        }
    }

    fun draw(canvas: Canvas) {
        // 1. Sky
        canvas.drawRect(0f, 0f, virtualWidth, groundY, skyPaint)

        // 2. Stars
        for (star in stars) {
            val alpha = ((sin(star.twinklePhase) * 0.5f + 0.5f) * 200 + 55).toInt()
            starPaint.alpha = alpha
            canvas.drawCircle(star.x, star.y, star.radius, starPaint)
        }

        // 3. Glowing Magical Moon
        val moonX = virtualWidth * 0.82f
        val moonY = virtualHeight * 0.22f
        canvas.drawCircle(moonX, moonY, 68f, moonGlowPaint)
        canvas.drawCircle(moonX, moonY, 46f, moonGlowPaint)
        // Crescent moon
        canvas.drawCircle(moonX, moonY, 34f, moonPaint)
        val shadowPaint = Paint().apply { color = 0xFF0A1226.toInt() }
        canvas.drawCircle(moonX - 12f, moonY - 6f, 30f, shadowPaint)

        // 4. Distant Mountains (0.12x speed)
        drawDistantMountains(canvas, mountainOffset)

        // 5. Midground Enchanted Trees & Bioluminescent Flora (0.45x speed)
        drawMidTrees(canvas, midTreesOffset)

        // 6. Floating Ambient Spores
        for (spore in spores) {
            sporePaint.color = spore.color
            sporePaint.alpha = 180
            canvas.drawCircle(spore.x, spore.y, spore.size, sporePaint)
        }

        // 7. Ground
        canvas.drawRect(0f, groundY, virtualWidth, virtualHeight, groundPaint)

        // Ground border neon-cyan accent line
        val borderPaint = Paint().apply {
            color = 0xFF00E5FF.toInt()
            alpha = 70
            strokeWidth = 3f
        }
        canvas.drawLine(0f, groundY, virtualWidth, groundY, borderPaint)
    }

    private fun drawDistantMountains(canvas: Canvas, offset: Float) {
        for (i in -1..2) {
            val baseX = i * virtualWidth - offset
            val path = Path().apply {
                moveTo(baseX, groundY)
                lineTo(baseX + 180f, groundY - 240f)
                lineTo(baseX + 400f, groundY - 140f)
                lineTo(baseX + 680f, groundY - 320f)
                lineTo(baseX + 960f, groundY - 180f)
                lineTo(baseX + 1300f, groundY - 280f)
                lineTo(baseX + 1600f, groundY - 160f)
                lineTo(baseX + virtualWidth, groundY - 220f)
                lineTo(baseX + virtualWidth, groundY)
                close()
            }
            canvas.drawPath(path, mountainPaint)
        }
    }

    private fun drawMidTrees(canvas: Canvas, offset: Float) {
        val segmentWidth = virtualWidth
        for (i in -1..1) {
            val baseX = i * segmentWidth - offset
            // Gnarled trees silhouette with glowing mushrooms on trunks
            for (t in 0..5) {
                val tx = baseX + t * 340f + 80f
                val trunkWidth = 38f
                val trunkHeight = 360f

                // Tree trunk
                canvas.drawRect(tx - trunkWidth / 2, groundY - trunkHeight, tx + trunkWidth / 2, groundY, midTreesPaint)
                // Tree canopy oval
                canvas.drawOval(
                    tx - 120f, groundY - trunkHeight - 90f,
                    tx + 120f, groundY - trunkHeight + 70f,
                    midTreesPaint
                )

                // Glowing mushroom on trunk
                mushroomGlowPaint.color = if (t % 2 == 0) 0xFF00E5FF.toInt() else 0xFFE040FB.toInt()
                mushroomGlowPaint.alpha = 220
                canvas.drawCircle(tx + trunkWidth / 2 + 5f, groundY - 120f, 7f, mushroomGlowPaint)
                mushroomGlowPaint.alpha = 70
                canvas.drawCircle(tx + trunkWidth / 2 + 5f, groundY - 120f, 15f, mushroomGlowPaint)
            }
        }
    }
}

