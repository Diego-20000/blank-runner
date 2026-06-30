package com.blankrunner

class Player(startX: Int, startY: Int) {
    var gridX = startX
        private set
    var gridY = startY
        private set

    var facing: Direction = Direction.RIGHT
        private set

    private var direction: Direction = Direction.NONE
    private var progress = 0f
    private val moveInterval = 0.14f

    val isMoving get() = direction != Direction.NONE

    fun setStart(x: Int, y: Int) {
        gridX = x
        gridY = y
        direction = Direction.NONE
        progress = 0f
    }

    fun trySetDirection(dir: Direction, level: Level) {
        facing = dir
        if (level.isPassable(gridX + dir.dx, gridY + dir.dy)) {
            direction = dir
            progress = 0f
        }
    }

    fun update(deltaTime: Float, level: Level) {
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

    fun visualX(): Float = gridX - direction.dx * (1f - progress)
    fun visualY(): Float = gridY - direction.dy * (1f - progress)

    fun fireIce(level: Level) {
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
                while (level.isInBounds(x, y) && level.tileAt(x, y) == TileType.EMPTY) {
                    level.setTile(x, y, TileType.ICE)
                    x += facing.dx
                    y += facing.dy
                }
            }
        }
    }
}
