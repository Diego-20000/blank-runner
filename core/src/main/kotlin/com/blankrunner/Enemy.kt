package com.blankrunner

import kotlin.random.Random

enum class EnemyType {
    PATROL,
    WANDERER,
    ICE_CRUSHER,
    CHASER
}

class Enemy(
    val type: EnemyType,
    var gridX: Int,
    var gridY: Int,
    initialDirection: Direction
) {
    private var direction: Direction = initialDirection
    private var moveTimer = 0f

    private var isChasePhase = false
    private var phaseTimer = 0f

    companion object {
        private val ALL_DIRECTIONS = listOf(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT)
        private const val WANDER_INTERVAL = 0.45f
        private const val CHASE_INTERVAL = 0.18f
        private const val PHASE_DURATION = 3f
        private const val CRUSH_PAUSE = 0.2f
    }

    fun update(deltaTime: Float, level: Level, playerX: Int, playerY: Int) {
        when (type) {
            EnemyType.PATROL -> updatePatrol(deltaTime, level)
            EnemyType.WANDERER -> updateWanderer(deltaTime, level)
            EnemyType.ICE_CRUSHER -> updateIceCrusher(deltaTime, level)
            EnemyType.CHASER -> updateChaser(deltaTime, level, playerX, playerY)
        }
    }

    private fun canEnter(level: Level, x: Int, y: Int) = level.isPassable(x, y)

    private fun updatePatrol(deltaTime: Float, level: Level) {
        moveTimer += deltaTime
        if (moveTimer < WANDER_INTERVAL) return
        moveTimer = 0f

        val nx = gridX + direction.dx
        val ny = gridY + direction.dy
        if (canEnter(level, nx, ny)) {
            gridX = nx
            gridY = ny
        } else {
            direction = opposite(direction)
        }
    }

    private fun updateWanderer(deltaTime: Float, level: Level) {
        moveTimer += deltaTime
        if (moveTimer < WANDER_INTERVAL) return
        moveTimer = 0f

        val nx = gridX + direction.dx
        val ny = gridY + direction.dy
        if (canEnter(level, nx, ny) && Random.nextFloat() > 0.25f) {
            gridX = nx
            gridY = ny
        } else {
            val options = ALL_DIRECTIONS.filter { canEnter(level, gridX + it.dx, gridY + it.dy) }
            if (options.isNotEmpty()) {
                direction = options.random()
                gridX += direction.dx
                gridY += direction.dy
            }
        }
    }

    private fun updateIceCrusher(deltaTime: Float, level: Level) {
        moveTimer += deltaTime
        if (moveTimer < WANDER_INTERVAL) return
        moveTimer = 0f

        val nx = gridX + direction.dx
        val ny = gridY + direction.dy
        when (level.tileAt(nx, ny)) {
            TileType.EMPTY -> {
                gridX = nx
                gridY = ny
            }
            TileType.ICE -> {
                level.setTile(nx, ny, TileType.EMPTY)
                moveTimer = -CRUSH_PAUSE
            }
            TileType.WALL -> {
                val options = ALL_DIRECTIONS.filter { level.tileAt(gridX + it.dx, gridY + it.dy) != TileType.WALL }
                direction = if (options.isNotEmpty()) options.random() else opposite(direction)
            }
        }
    }

    private fun updateChaser(deltaTime: Float, level: Level, playerX: Int, playerY: Int) {
        phaseTimer += deltaTime
        if (phaseTimer >= PHASE_DURATION) {
            phaseTimer = 0f
            isChasePhase = !isChasePhase
        }

        moveTimer += deltaTime
        val interval = if (isChasePhase) CHASE_INTERVAL else WANDER_INTERVAL
        if (moveTimer < interval) return
        moveTimer = 0f

        if (isChasePhase) {
            val dx = playerX - gridX
            val dy = playerY - gridY
            val preferred = if (kotlin.math.abs(dx) > kotlin.math.abs(dy)) {
                listOf(if (dx > 0) Direction.RIGHT else Direction.LEFT, if (dy > 0) Direction.UP else Direction.DOWN)
            } else {
                listOf(if (dy > 0) Direction.UP else Direction.DOWN, if (dx > 0) Direction.RIGHT else Direction.LEFT)
            }
            for (dir in preferred) {
                val nx = gridX + dir.dx
                val ny = gridY + dir.dy
                if (canEnter(level, nx, ny)) {
                    direction = dir
                    gridX = nx
                    gridY = ny
                    return
                }
            }
        } else {
            val nx = gridX + direction.dx
            val ny = gridY + direction.dy
            if (canEnter(level, nx, ny) && Random.nextFloat() > 0.3f) {
                gridX = nx
                gridY = ny
            } else {
                val options = ALL_DIRECTIONS.filter { canEnter(level, gridX + it.dx, gridY + it.dy) }
                if (options.isNotEmpty()) {
                    direction = options.random()
                    gridX += direction.dx
                    gridY += direction.dy
                }
            }
        }
    }

    private fun opposite(dir: Direction): Direction = when (dir) {
        Direction.UP -> Direction.DOWN
        Direction.DOWN -> Direction.UP
        Direction.LEFT -> Direction.RIGHT
        Direction.RIGHT -> Direction.LEFT
        Direction.NONE -> Direction.NONE
    }
}
