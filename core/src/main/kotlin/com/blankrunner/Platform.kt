package com.blankrunner

class Platform(
    var x: Float,
    var y: Float,
    val width: Float,
    val height: Float = 16f,
    val isDynamic: Boolean = false
) {
    var velocityX: Float = 0f

    fun update(deltaTime: Float) {
        if (isDynamic) {
            x += velocityX * deltaTime
        }
    }

    fun getLeft() = x
    fun getRight() = x + width
    fun getTop() = y + height
    fun getBottom() = y

    fun contains(px: Float, py: Float): Boolean {
        return px >= x && px <= x + width && py >= y && py <= y + height
    }
}
