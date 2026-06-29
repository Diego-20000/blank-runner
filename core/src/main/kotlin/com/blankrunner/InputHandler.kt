package com.blankrunner

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input

class InputHandler {
    var isLeftPressed = false
    var isRightPressed = false
    var isJumpPressed = false

    private val screenWidth = 800f
    private val screenHeight = 600f
    private var lastTouchX = -1f

    fun update() {
        // Keyboard input (desktop testing)
        isLeftPressed = Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)
        isRightPressed = Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)
        isJumpPressed = Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.W)

        // Touch input (mobile)
        if (Gdx.input.isTouched) {
            val touchX = Gdx.input.x.toFloat()
            val touchY = Gdx.input.y.toFloat()

            // Determine left/right based on screen position
            val midpoint = screenWidth / 2f
            if (touchX < midpoint) {
                isLeftPressed = true
                isRightPressed = false
            } else {
                isLeftPressed = false
                isRightPressed = true
            }

            // Jump on top portion of screen (top third)
            if (touchY < screenHeight / 3f) {
                isJumpPressed = !isTouchProcessed(touchX)
                lastTouchX = touchX
            }
        } else {
            isLeftPressed = false
            isRightPressed = false
        }
    }

    private fun isTouchProcessed(x: Float): Boolean {
        return lastTouchX >= 0f && Math.abs(x - lastTouchX) < 10f
    }
}
