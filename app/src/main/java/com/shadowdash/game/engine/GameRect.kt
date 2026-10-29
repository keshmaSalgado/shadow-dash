package com.shadowdash.game.engine

/**
 * Platform-independent, high-performance bounding box for fast collision detection.
 */
data class GameRect(
    var left: Float = 0f,
    var top: Float = 0f,
    var right: Float = 0f,
    var bottom: Float = 0f
) {
    val width: Float get() = (right - left).coerceAtLeast(0f)
    val height: Float get() = (bottom - top).coerceAtLeast(0f)

    fun set(l: Float, t: Float, r: Float, b: Float) {
        left = l
        top = t
        right = r
        bottom = b
    }

    fun intersects(other: GameRect): Boolean {
        return left < other.right && right > other.left && top < other.bottom && bottom > other.top
    }

    companion object {
        fun intersects(a: GameRect, b: GameRect): Boolean {
            return a.intersects(b)
        }
    }
}

