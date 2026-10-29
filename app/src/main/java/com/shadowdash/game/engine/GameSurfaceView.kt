package com.shadowdash.game.engine

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView

class GameSurfaceView(
    context: Context,
    val engine: GameEngine
) : SurfaceView(context), SurfaceHolder.Callback, Runnable {

    private var gameThread: Thread? = null
    @Volatile
    private var isRunning = false
    private val surfaceHolder: SurfaceHolder = holder

    private val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            engine.onJump()
            return true
        }

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            if (e1 != null && (e2.y - e1.y > 50f || velocityY > 400f)) {
                engine.onSlide()
                return true
            }
            return false
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            if (distanceY < -25f) { // downward swipe gesture
                engine.onSlide()
                return true
            }
            return false
        }
    })

    init {
        surfaceHolder.addCallback(this)
        isFocusable = true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (gestureDetector.onTouchEvent(event)) {
            return true
        }
        if (event.action == MotionEvent.ACTION_DOWN) {
            engine.onJump()
            return true
        }
        return super.onTouchEvent(event)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        startLoop()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        stopLoop()
    }

    fun resumeLoop() {
        if (!isRunning) {
            startLoop()
        }
    }

    fun pauseLoop() {
        stopLoop()
    }

    @Synchronized
    private fun startLoop() {
        if (isRunning) return
        isRunning = true
        gameThread = Thread(this, "ShadowDash-GameThread").apply {
            start()
        }
    }

    @Synchronized
    private fun stopLoop() {
        isRunning = false
        try {
            gameThread?.join(500)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }
        gameThread = null
    }

    override fun run() {
        var lastTime = System.nanoTime()
        val targetFrameTimeNs = 1_000_000_000L / 60L // Target 60 FPS

        while (isRunning) {
            val now = System.nanoTime()
            val dt = (now - lastTime) / 1_000_000_000f
            lastTime = now

            // Update game physics & logic
            engine.update(dt)

            // Render frame
            var canvas: Canvas? = null
            try {
                canvas = surfaceHolder.lockCanvas()
                if (canvas != null) {
                    synchronized(surfaceHolder) {
                        renderFrame(canvas)
                    }
                }
            } catch (e: Exception) {
                // Ignore transient surface errors
            } finally {
                if (canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas)
                    } catch (e: Exception) {
                        // Surface was already released
                    }
                }
            }

            // Frame rate governor to prevent overheating and save battery
            val frameDuration = System.nanoTime() - now
            val sleepTimeNs = targetFrameTimeNs - frameDuration
            if (sleepTimeNs > 1_000_000L) {
                try {
                    Thread.sleep(sleepTimeNs / 1_000_000L)
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                    break
                }
            }
        }
    }

    private fun renderFrame(canvas: Canvas) {
        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()
        if (viewWidth <= 0f || viewHeight <= 0f) return

        canvas.drawColor(Color.parseColor("#04060E"))

        // Compute aspect-fill or letterbox transformation from virtual resolution (1920x1080)
        val scaleX = viewWidth / engine.virtualWidth
        val scaleY = viewHeight / engine.virtualHeight
        val scale = scaleX.coerceAtLeast(scaleY) // Fill screen seamlessly

        val offsetX = (viewWidth - engine.virtualWidth * scale) / 2f
        val offsetY = (viewHeight - engine.virtualHeight * scale) / 2f

        canvas.save()
        canvas.translate(offsetX, offsetY)
        canvas.scale(scale, scale)

        // Draw entire game world
        engine.draw(canvas)

        canvas.restore()
    }
}

