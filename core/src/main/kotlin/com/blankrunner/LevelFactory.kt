package com.blankrunner

data class LevelData(
    val level: Level,
    val playerStartX: Int,
    val playerStartY: Int,
    val fruits: List<Fruit>,
    val enemies: List<Enemy>
)

object LevelFactory {
    const val COLS = 15
    const val ROWS = 9

    fun level1(): LevelData {
        val level = Level(COLS, ROWS)
        level.reset()

        val centerStructure = listOf(
            7 to 3, 6 to 4, 7 to 4, 8 to 4, 7 to 5
        )
        for ((x, y) in centerStructure) {
            level.setTile(x, y, TileType.WALL)
        }

        for (y in 2..6) {
            level.setTile(3, y, TileType.ICE)
            level.setTile(11, y, TileType.ICE)
        }

        val fruits = listOf(
            Fruit(FruitType.BERRY, FruitBehavior.STATIONARY, 13, 7),
            Fruit(FruitType.MELON, FruitBehavior.MOBILE, 7, 1),
            Fruit(FruitType.GRAPE, FruitBehavior.TELEPORTING, 2, 7)
        )

        val enemies = listOf(
            Enemy(EnemyType.PATROL, 5, 7, Direction.RIGHT),
            Enemy(EnemyType.WANDERER, 9, 2, Direction.UP),
            Enemy(EnemyType.ICE_CRUSHER, 12, 5, Direction.LEFT),
            Enemy(EnemyType.CHASER, 7, 6, Direction.DOWN)
        )

        return LevelData(level, 1, 1, fruits, enemies)
    }
}
