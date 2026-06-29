package com.blankrunner

class PhysicsEngine {
    private val gravity = -600f

    fun applyGravity(player: Player, deltaTime: Float) {
        player.velocityY += gravity * deltaTime
    }

    fun checkCollisions(player: Player, platforms: List<Platform>) {
        for (platform in platforms) {
            if (isCollidingWith(player, platform)) {
                handleCollision(player, platform)
            }
        }

        if (player.y < 0) {
            player.y = 0f
            player.velocityY = 0f
            player.isJumping = false
        }
    }

    private fun isCollidingWith(player: Player, platform: Platform): Boolean {
        return player.getRight() > platform.getLeft() &&
                player.getLeft() < platform.getRight() &&
                player.getTop() > platform.getBottom() &&
                player.getBottom() < platform.getTop()
    }

    private fun handleCollision(player: Player, platform: Platform) {
        val overlapTop = player.getBottom() - platform.getTop()
        val overlapBottom = platform.getBottom() - player.getTop()
        val overlapLeft = player.getRight() - platform.getLeft()
        val overlapRight = platform.getRight() - player.getLeft()

        val minOverlap = minOf(
            kotlin.math.abs(overlapTop),
            kotlin.math.abs(overlapBottom),
            kotlin.math.abs(overlapLeft),
            kotlin.math.abs(overlapRight)
        )

        when (minOverlap) {
            kotlin.math.abs(overlapTop) -> {
                player.y = platform.getTop()
                player.velocityY = 0f
                player.isJumping = false
            }
            kotlin.math.abs(overlapBottom) -> {
                player.y = platform.getBottom() - player.height
                player.velocityY = 0f
            }
            kotlin.math.abs(overlapLeft) -> player.x = platform.getLeft() - player.width
            kotlin.math.abs(overlapRight) -> player.x = platform.getRight()
        }
    }
}
