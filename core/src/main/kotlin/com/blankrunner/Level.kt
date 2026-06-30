package com.blankrunner

class Level(val cols: Int, val rows: Int) {
    val tileSize = 60f

    private val tiles: Array<TileType> = Array(cols * rows) { TileType.EMPTY }

    private fun index(x: Int, y: Int) = y * cols + x

    fun isInBounds(x: Int, y: Int) = x in 0 until cols && y in 0 until rows

    fun tileAt(x: Int, y: Int): TileType {
        if (!isInBounds(x, y)) return TileType.WALL
        return tiles[index(x, y)]
    }

    fun setTile(x: Int, y: Int, type: TileType) {
        if (isInBounds(x, y)) tiles[index(x, y)] = type
    }

    fun isPassable(x: Int, y: Int): Boolean = tileAt(x, y) == TileType.EMPTY

    fun isBlocking(x: Int, y: Int): Boolean = tileAt(x, y) != TileType.EMPTY

    fun worldX(gridX: Int): Float = gridX * tileSize
    fun worldY(gridY: Int): Float = gridY * tileSize
}
