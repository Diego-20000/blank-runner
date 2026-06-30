package com.blankrunner

class Player(startX: Int, startY: Int) {
    var gridX = startX
        private set
    var gridY = startY
        private set

    /** Cell the player occupied at the start of the current frame (for swap-collision checks). */
    var prevX = startX
        private set
    var prevY = startY
        private set

    var facing: Direction = Direction.RIGHT
        private set

    private var direction: Direction = Direction.NONE
    private var progress = 0f
    private val moveInterval = 0.13f

    val isMoving get() = direction != Direction.NONE

    fun setStart(x: Int, y: Int) {
        gridX = x
        gridY = y
        prevX = x
        prevY = y
        direction = Direction.NONE
        facing = Direction.RIGHT
        progress = 0f
    }

    fun trySetDirection(dir: Direction, level: Level) {
        if (dir == Direction.NONE) return
        facing = dir
        if (level.isPassable(gridX + dir.dx, gridY + dir.dy)) {
            direction = dir
            progress = 0f
        }
    }

    fun update(deltaTime: Float, level: Level) {
        prevX = gridX
        prevY = gridY
        if (direction == Direction.NONE) return

        progress += deltaTime / moveInterval
        if (progress >= 1f) {
            progress -= 1f
            gridX += direction.dx
            gridY += direction.dy

            val nextX = gridX + direction.dx
            val nextY = gridY + direction.dy
            if (!level.isPassable(nextX, nextY)) {
                direction = Direction.NONE
                progress = 0f
            }
        }
    }

    /** Smooth render position in tile units. */
    fun visualX(): Float = gridX - direction.dx * (1f - progress)
    fun visualY(): Float = gridY - direction.dy * (1f - progress)

    /**
     * Shoots ice in the facing direction. If the first facing tile is ice, the
     * whole contiguous ice line is shattered. Otherwise a line of ice blocks is
     * created across empty tiles until it reaches a wall, an occupied tile, or
     * the level edge. [isOccupied] reports tiles held by enemies/fruit so the
     * player can wall things off but never freeze a creature or fruit in place.
     */
    fun fireIce(level: Level, isOccupied: (Int, Int) -> Boolean) {
        val fx = gridX + facing.dx
        val fy = gridY + facing.dy
        if (!level.isInBounds(fx, fy)) return

        when (level.tileAt(fx, fy)) {
            TileType.WALL -> return
            TileType.ICE -> {
                var x = fx
                var y = fy
                while (level.tileAt(x, y) == TileType.ICE) {
                    level.setTile(x, y, TileType.EMPTY)
                    x += facing.dx
                    y += facing.dy
                }
            }
            TileType.EMPTY -> {
                var x = fx
                var y = fy
                while (level.isInBounds(x, y) &&
                    level.tileAt(x, y) == TileType.EMPTY &&
                    !isOccupied(x, y)
                ) {
                    level.setTile(x, y, TileType.ICE)
                    x += facing.dx
                    y += facing.dy
                }
            }
        }
    }
}
