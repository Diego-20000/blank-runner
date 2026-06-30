package com.blankrunner

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.math.Rectangle

interface GameInputListener {
    fun onSwipe(direction: Direction)
    fun onFireButton()
}

/**
 * Touch coordinates are converted from screen space (y-down, origin top-left)
 * to the game's y-up world space so they line up with [fireButtonBounds].
 */
class SwipeInputProcessor(
    private val listener: GameInputListener,
    private val fireButtonBounds: () -> Rectangle
) : InputAdapter() {

    private var startX = 0f
    private var startY = 0f
    private var startedOnButton = false
    private val swipeThreshold = 40f

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        startX = screenX.toFloat()
        startY = Gdx.graphics.height - screenY.toFloat()
        startedOnButton = fireButtonBounds().contains(startX, startY)
        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val endX = screenX.toFloat()
        val endY = Gdx.graphics.height - screenY.toFloat()

        if (startedOnButton) {
            if (fireButtonBounds().contains(endX, endY)) {
                listener.onFireButton()
            }
            return true
        }

        val dx = endX - startX
        val dy = endY - startY
        if (kotlin.math.abs(dx) < swipeThreshold && kotlin.math.abs(dy) < swipeThreshold) {
            return true
        }

        val direction = if (kotlin.math.abs(dx) > kotlin.math.abs(dy)) {
            if (dx > 0) Direction.RIGHT else Direction.LEFT
        } else {
            if (dy > 0) Direction.UP else Direction.DOWN
        }
        listener.onSwipe(direction)
        return true
    }
}
