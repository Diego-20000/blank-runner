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
    startX: Int,
    startY: Int
) {
    var gridX = startX
        private set
    var gridY = startY
        private set

    var renderX = startX.toFloat()
        private set
    var renderY = startY.toFloat()
        private set

    var collected = false

    private var actionTimer = 0f
    private val actionInterval = if (behavior == FruitBehavior.TELEPORTING) 3.5f else 0.45f

    companion object {
        private const val RENDER_SMOOTH = 12f
    }

    fun update(deltaTime: Float, level: Level, playerX: Int, playerY: Int) {
        if (collected) return

        actionTimer += deltaTime
        if (actionTimer >= actionInterval) {
            actionTimer = 0f
            when (behavior) {
                FruitBehavior.STATIONARY -> {}
                FruitBehavior.MOBILE -> fleeFromPlayer(level, playerX, playerY)
                FruitBehavior.TELEPORTING -> teleportIfNotCornered(level, playerX, playerY)
            }
        }

        val t = (deltaTime * RENDER_SMOOTH).coerceAtMost(1f)
        renderX += (gridX - renderX) * t
        renderY += (gridY - renderY) * t
    }

    private fun manhattanToPlayer(playerX: Int, playerY: Int) =
        kotlin.math.abs(gridX - playerX) + kotlin.math.abs(gridY - playerY)

    private fun fleeFromPlayer(level: Level, playerX: Int, playerY: Int) {
        if (manhattanToPlayer(playerX, playerY) > 4) return

        val best = listOf(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT)
            .filter { level.isPassable(gridX + it.dx, gridY + it.dy) }
            .maxByOrNull { dir ->
                val nx = gridX + dir.dx
                val ny = gridY + dir.dy
                (nx - playerX) * (nx - playerX) + (ny - playerY) * (ny - playerY)
            } ?: return

        gridX += best.dx
        gridY += best.dy
    }

    private fun teleportIfNotCornered(level: Level, playerX: Int, playerY: Int) {
        // Stays put once the player is right next to it, so it can be grabbed.
        if (manhattanToPlayer(playerX, playerY) <= 1) return

        for (attempt in 0 until 30) {
            val nx = Random.nextInt(1, level.cols - 1)
            val ny = Random.nextInt(1, level.rows - 1)
            if (level.isPassable(nx, ny) && (nx != playerX || ny != playerY)) {
                gridX = nx
                gridY = ny
                return
            }
        }
    }
}
