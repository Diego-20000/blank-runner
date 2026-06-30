package com.blankrunner

data class LevelData(
    val level: Level,
    val playerStartX: Int,
    val playerStartY: Int,
    val fruits: List<Fruit>,
    val enemies: List<Enemy>
)

/**
 * Builds levels from ASCII maps. The top string is the highest row (so maps read
 * the way they look on screen). Legend:
 *
 *   #  wall            *  ice
 *   P  player start    .  / space  empty
 *   o  stationary fruit   x  mobile fruit   t  teleporting fruit
 *   1  patrol   2  wanderer   3  ice-crusher   4  chaser
 */
object LevelFactory {
    const val COLS = 15
    const val ROWS = 9

    val levelCount: Int get() = maps.size

    private val maps: List<List<String>> = listOf(
        // Level 1 — gentle: stationary fruit, two simple enemies.
        listOf(
            "###############",
            "#P...........o#",
            "#....*...*....#",
            "#....*...*....#",
            "#..o.*.#.*..o.#",
            "#....*...*....#",
            "#....*...*....#",
            "#.1.......2...#",
            "###############"
        ),
        // Level 2 — interior walls, an ice-crusher and a fleeing fruit.
        listOf(
            "###############",
            "#P....o....x..#",
            "#..#......#...#",
            "#..#.*..*.#...#",
            "#....*..*.....#",
            "#..#.*..*.#...#",
            "#..#......#3..#",
            "#.1....o....2.#",
            "###############"
        ),
        // Level 3 — chaser plus teleporting fruit; tightest layout.
        listOf(
            "###############",
            "#P....t....o..#",
            "#..*.....*....#",
            "#..*..#..*....#",
            "#4.*..#..*..3.#",
            "#..*..#..*....#",
            "#..*.....*....#",
            "#.1...x...2.t.#",
            "###############"
        )
    )

    private val enemyDirections = mapOf(
        EnemyType.PATROL to Direction.RIGHT,
        EnemyType.WANDERER to Direction.UP,
        EnemyType.ICE_CRUSHER to Direction.LEFT,
        EnemyType.CHASER to Direction.DOWN
    )

    fun load(index: Int): LevelData {
        val map = maps[index]
        require(map.size == ROWS) { "Level $index must have $ROWS rows, has ${map.size}" }

        val level = Level(COLS, ROWS)
        val fruits = mutableListOf<Fruit>()
        val enemies = mutableListOf<Enemy>()
        var playerX = 1
        var playerY = 1
        var fruitColorCounter = 0

        for (r in 0 until ROWS) {
            val row = map[r]
            require(row.length == COLS) {
                "Level $index row $r must have $COLS columns, has ${row.length}: \"$row\""
            }
            val y = ROWS - 1 - r
            for (x in 0 until COLS) {
                when (row[x]) {
                    '#' -> level.setTile(x, y, TileType.WALL)
                    '*' -> level.setTile(x, y, TileType.ICE)
                    'P' -> {
                        playerX = x
                        playerY = y
                    }
                    'o', 'x', 't' -> {
                        val behavior = when (row[x]) {
                            'x' -> FruitBehavior.MOBILE
                            't' -> FruitBehavior.TELEPORTING
                            else -> FruitBehavior.STATIONARY
                        }
                        val fruitType = FruitType.values()[fruitColorCounter % FruitType.values().size]
                        fruitColorCounter++
                        fruits.add(Fruit(fruitType, behavior, x, y))
                    }
                    '1', '2', '3', '4' -> {
                        val enemyType = when (row[x]) {
                            '1' -> EnemyType.PATROL
                            '2' -> EnemyType.WANDERER
                            '3' -> EnemyType.ICE_CRUSHER
                            else -> EnemyType.CHASER
                        }
                        val dir = enemyDirections[enemyType] ?: Direction.RIGHT
                        enemies.add(Enemy(enemyType, x, y, dir))
                    }
                    '.', ' ' -> {}
                    else -> throw IllegalArgumentException(
                        "Level $index has unknown tile '${row[x]}' at ($x,$y)"
                    )
                }
            }
        }

        return LevelData(level, playerX, playerY, fruits, enemies)
    }
}
