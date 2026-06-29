package com.blankrunner

class Player(
    var x: Float = 100f,
    var y: Float = 150f,
    val width: Float = 16f,
    val height: Float = 16f
) {
    var velocityX: Float = 0f
    var velocityY: Float = 0f
    var isJumping: Boolean = false

    private val moveSpeed = 150f
    private val jumpPower = 400f

    fun update(deltaTime: Float, inputHandler: InputHandler, physics: PhysicsEngine) {
        handleInput(inputHandler)

        velocityX = when {
            inputHandler.isLeftPressed && !inputHandler.isRightPressed -> -moveSpeed
            inputHandler.isRightPressed && !inputHandler.isLeftPressed -> moveSpeed
            else -> 0f
        }

        if (inputHandler.isJumpPressed && !isJumping) {
            velocityY = jumpPower
            isJumping = true
        }

        physics.applyGravity(this, deltaTime)

        x += velocityX * deltaTime
        y += velocityY * deltaTime

        if (x < 0) x = 0f
        if (x + width > 800) x = 800f - width
    }

    private fun handleInput(inputHandler: InputHandler) {
        inputHandler.update()
    }

    fun getLeft() = x
    fun getRight() = x + width
    fun getTop() = y + height
    fun getBottom() = y
}
