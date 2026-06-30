package com.blankrunner

import kotlin.random.Random

enum class FruitType {
    BERRY,
    GRAPE,
    MELON
}

enum class FruitBehavior {
    STATIONARY,
    MOBILE,
    TELEPORTING
}

class Fruit(
    val type: FruitType,
    val behavior: FruitBehavior,
    var gridX: Int,
    var gridY: Int
) {
    var collected = false
    private var actionTimer = 0f
    private val actionInterval = if (behavior == FruitBehavior.TELEPORTING) 4f else 0.4f

    fun update(deltaTime: Float, level: Level, playerX: Int, playerY: Int) {
        if (collected) return
        actionTimer += deltaTime
        if (actionTimer < actionInterval) return
        actionTimer = 0f

        when (behavior) {
            FruitBehavior.STATIONARY -> {}
            FruitBehavior.MOBILE -> fleeFromPlayer(level, playerX, playerY)
            FruitBehavior.TELEPORTING -> teleportIfExposed(level)
        }
    }

    private fun fleeFromPlayer(level: Level, playerX: Int, playerY: Int) {
        val dx = gridX - playerX
        val dy = gridY - playerY
        if (kotlin.math.abs(dx) > 4 || kotlin.math.abs(dy) > 4) return

        val candidates = listOf(
            Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT
        ).sortedByDescending { dir ->
            val nx = gridX + dir.dx
            val ny = gridY + dir.dy
            -((nx - playerX) * (nx - playerX) + (ny - playerY) * (ny - playerY))
        }

        for (dir in candidates) {
            val nx = gridX + dir.dx
            val ny = gridY + dir.dy
            if (level.isPassable(nx, ny)) {
                gridX = nx
                gridY = ny
                return
            }
        }
    }

    private fun isBoxedIn(level: Level): Boolean {
        return level.isBlocking(gridX, gridY + 1) &&
            level.isBlocking(gridX, gridY - 1) &&
            level.isBlocking(gridX - 1, gridY) &&
            level.isBlocking(gridX + 1, gridY)
    }

    private fun teleportIfExposed(level: Level) {
        if (isBoxedIn(level)) return

        for (attempt in 0 until 20) {
            val nx = Random.nextInt(1, level.cols - 1)
            val ny = Random.nextInt(1, level.rows - 1)
            if (level.isPassable(nx, ny)) {
                gridX = nx
                gridY = ny
                return
            }
        }
    }
}
