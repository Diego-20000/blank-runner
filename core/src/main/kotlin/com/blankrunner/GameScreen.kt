package com.blankrunner

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport

class GameScreen : Screen, GameInputListener {
    private val shapeRenderer = ShapeRenderer()
    private val batch = SpriteBatch()
    private val hud = Hud()
    private val gameState = GameState()

    private val camera = OrthographicCamera()
    private val viewport: Viewport

    private var phase = GamePhase.TITLE
    private var currentLevelIndex = 0
    private var levelStartScore = 0

    private val initialData = LevelFactory.load(0)
    private var level: Level = initialData.level
    private var fruits: List<Fruit> = initialData.fruits
    private var enemies: List<Enemy> = initialData.enemies
    private val player = Player(initialData.playerStartX, initialData.playerStartY)

    private val tileSize: Float = level.tileSize

    init {
        val worldWidth = LevelFactory.COLS * tileSize
        val worldHeight = LevelFactory.ROWS * tileSize
        camera.position.set(worldWidth / 2f, worldHeight / 2f, 0f)
        viewport = FitViewport(worldWidth, worldHeight, camera)
    }

    // ---- level / phase management -------------------------------------------

    private fun loadLevel(index: Int) {
        currentLevelIndex = index
        val data = LevelFactory.load(index)
        level = data.level
        fruits = data.fruits
        enemies = data.enemies
        player.setStart(data.playerStartX, data.playerStartY)
        levelStartScore = gameState.score
        gameState.startLevelTimer()
        phase = GamePhase.PLAYING
    }

    private fun retryLevel() {
        gameState.score = levelStartScore
        loadLevel(currentLevelIndex)
    }

    private fun advancePhase() {
        when (phase) {
            GamePhase.TITLE -> {
                gameState.fullReset()
                loadLevel(0)
            }
            GamePhase.LEVEL_COMPLETE -> loadLevel(currentLevelIndex + 1)
            GamePhase.GAME_OVER -> retryLevel()
            GamePhase.CAUGHT -> retryLevel()
            GamePhase.VICTORY -> phase = GamePhase.TITLE
            GamePhase.PLAYING -> {}
        }
    }

    // ---- input --------------------------------------------------------------

    override fun onSwipe(direction: Direction) {
        if (phase == GamePhase.PLAYING) {
            player.trySetDirection(direction, level)
        } else {
            advancePhase()
        }
    }

    override fun onTap() {
        if (phase != GamePhase.PLAYING) advancePhase()
    }

    override fun onFireButton() {
        if (phase == GamePhase.PLAYING) {
            player.fireIce(level) { x, y -> isOccupied(x, y) }
        } else {
            advancePhase()
        }
    }

    private fun isOccupied(x: Int, y: Int): Boolean {
        if (enemies.any { it.gridX == x && it.gridY == y }) return true
        return fruits.any { !it.collected && it.gridX == x && it.gridY == y }
    }

    // ---- update -------------------------------------------------------------

    override fun render(deltaTime: Float) {
        if (phase == GamePhase.PLAYING) update(deltaTime)
        draw()
    }

    private fun update(deltaTime: Float) {
        if (gameState.tick(deltaTime)) {
            phase = GamePhase.GAME_OVER
            return
        }

        player.update(deltaTime, level)
        for (enemy in enemies) enemy.update(deltaTime, level, player.gridX, player.gridY)
        for (fruit in fruits) fruit.update(deltaTime, level, player.gridX, player.gridY)

        for (fruit in fruits) {
            if (!fruit.collected && fruit.gridX == player.gridX && fruit.gridY == player.gridY) {
                fruit.collected = true
                gameState.addScore(10)
            }
        }

        if (fruits.all { it.collected }) {
            phase = if (currentLevelIndex + 1 < LevelFactory.levelCount) {
                GamePhase.LEVEL_COMPLETE
            } else {
                GamePhase.VICTORY
            }
            return
        }

        if (isCaught()) phase = GamePhase.CAUGHT
    }

    /** Direct overlap, plus the swap case where player and enemy trade cells in one frame. */
    private fun isCaught(): Boolean = enemies.any { e ->
        (e.gridX == player.gridX && e.gridY == player.gridY) ||
            (e.gridX == player.prevX && e.gridY == player.prevY &&
                e.prevX == player.gridX && e.prevY == player.gridY)
    }

    // ---- rendering ----------------------------------------------------------

    private fun draw() {
        Gdx.gl.glClearColor(0.93f, 0.96f, 1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        if (phase != GamePhase.TITLE) {
            viewport.apply()
            camera.update()
            shapeRenderer.projectionMatrix = camera.combined
            drawTiles()
            drawFruits()
            drawEnemies()
            drawPlayer()
            hud.renderPlaying(batch, shapeRenderer, gameState, fruits, currentLevelIndex + 1)
        }

        if (phase != GamePhase.PLAYING) {
            hud.renderOverlay(batch, shapeRenderer, phase, currentLevelIndex + 1, gameState.score)
        }
    }

    private fun drawTiles() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        for (x in 0 until level.cols) {
            for (y in 0 until level.rows) {
                val wx = level.worldX(x)
                val wy = level.worldY(y)
                when (level.tileAt(x, y)) {
                    TileType.WALL -> {
                        shapeRenderer.color = Color(0.24f, 0.27f, 0.34f, 1f)
                        shapeRenderer.rect(wx, wy, tileSize, tileSize)
                        shapeRenderer.color = Color(0.32f, 0.36f, 0.44f, 1f)
                        shapeRenderer.rect(wx, wy + tileSize * 0.8f, tileSize, tileSize * 0.2f)
                    }
                    TileType.ICE -> {
                        shapeRenderer.color = Color(0.62f, 0.82f, 0.96f, 1f)
                        shapeRenderer.rect(wx, wy, tileSize, tileSize)
                        shapeRenderer.color = Color(0.80f, 0.92f, 1f, 1f)
                        shapeRenderer.rect(
                            wx + tileSize * 0.12f, wy + tileSize * 0.5f,
                            tileSize * 0.4f, tileSize * 0.32f
                        )
                    }
                    TileType.EMPTY -> {}
                }
            }
        }
        shapeRenderer.end()

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color(0.84f, 0.89f, 0.94f, 1f)
        for (x in 0..level.cols) {
            shapeRenderer.line(x * tileSize, 0f, x * tileSize, level.rows * tileSize)
        }
        for (y in 0..level.rows) {
            shapeRenderer.line(0f, y * tileSize, level.cols * tileSize, y * tileSize)
        }
        shapeRenderer.end()
    }

    private fun fruitColor(type: FruitType): Color = when (type) {
        FruitType.BERRY -> Color(0.85f, 0.20f, 0.28f, 1f)
        FruitType.GRAPE -> Color(0.55f, 0.30f, 0.78f, 1f)
        FruitType.MELON -> Color(0.36f, 0.72f, 0.38f, 1f)
    }

    private fun drawFruits() {
        for (fruit in fruits) {
            if (fruit.collected) continue
            val cx = fruit.renderX * tileSize + tileSize / 2f
            val cy = fruit.renderY * tileSize + tileSize / 2f
            val r = tileSize * 0.26f

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
            shapeRenderer.color = Color(0.30f, 0.55f, 0.30f, 1f)
            shapeRenderer.rect(cx - tileSize * 0.03f, cy + r * 0.6f, tileSize * 0.06f, tileSize * 0.14f)
            shapeRenderer.color = fruitColor(fruit.type)
            shapeRenderer.circle(cx, cy, r)
            shapeRenderer.color = Color(1f, 1f, 1f, 0.55f)
            shapeRenderer.circle(cx - r * 0.35f, cy + r * 0.35f, r * 0.22f)
            shapeRenderer.end()

            shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
            shapeRenderer.color = Color(0.15f, 0.15f, 0.18f, 1f)
            shapeRenderer.circle(cx, cy, r)
            shapeRenderer.end()
        }
    }

    private fun enemyColor(type: EnemyType): Color = when (type) {
        EnemyType.PATROL -> Color(0.95f, 0.60f, 0.15f, 1f)
        EnemyType.WANDERER -> Color(0.93f, 0.83f, 0.22f, 1f)
        EnemyType.ICE_CRUSHER -> Color(0.42f, 0.48f, 0.95f, 1f)
        EnemyType.CHASER -> Color(0.90f, 0.27f, 0.52f, 1f)
    }

    private fun drawEnemies() {
        for (enemy in enemies) {
            val cx = enemy.renderX * tileSize + tileSize / 2f
            val cy = enemy.renderY * tileSize + tileSize / 2f
            val r = tileSize * 0.34f

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
            shapeRenderer.color = enemyColor(enemy.type)
            shapeRenderer.circle(cx, cy, r)
            // eyes
            shapeRenderer.color = Color.WHITE
            shapeRenderer.circle(cx - r * 0.35f, cy + r * 0.15f, r * 0.26f)
            shapeRenderer.circle(cx + r * 0.35f, cy + r * 0.15f, r * 0.26f)
            shapeRenderer.color = Color(0.10f, 0.10f, 0.12f, 1f)
            shapeRenderer.circle(cx - r * 0.35f, cy + r * 0.15f, r * 0.12f)
            shapeRenderer.circle(cx + r * 0.35f, cy + r * 0.15f, r * 0.12f)
            shapeRenderer.end()

            shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
            shapeRenderer.color = Color(0.12f, 0.12f, 0.15f, 1f)
            shapeRenderer.circle(cx, cy, r)
            shapeRenderer.end()
        }
    }

    private fun drawPlayer() {
        val cx = player.visualX() * tileSize + tileSize / 2f
        val cy = player.visualY() * tileSize + tileSize / 2f
        val r = tileSize * 0.34f

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color(0.98f, 0.98f, 1f, 1f)
        shapeRenderer.circle(cx, cy, r)
        shapeRenderer.color = Color(0.62f, 0.82f, 0.96f, 1f)
        shapeRenderer.circle(cx, cy - r * 0.1f, r * 0.7f)
        shapeRenderer.end()

        // Eyes look toward the facing direction.
        val fdx = player.facing.dx.toFloat()
        val fdy = player.facing.dy.toFloat()
        val perpX = -fdy
        val perpY = fdx
        val eyeFwd = r * 0.18f
        val eyeSep = r * 0.34f
        val ex = cx + fdx * eyeFwd
        val ey = cy + fdy * eyeFwd + r * 0.12f

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color(0.12f, 0.12f, 0.16f, 1f)
        shapeRenderer.circle(ex + perpX * eyeSep, ey + perpY * eyeSep, r * 0.12f)
        shapeRenderer.circle(ex - perpX * eyeSep, ey - perpY * eyeSep, r * 0.12f)
        shapeRenderer.end()

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color(0.20f, 0.30f, 0.42f, 1f)
        shapeRenderer.circle(cx, cy, r)
        shapeRenderer.end()
    }

    // ---- lifecycle ----------------------------------------------------------

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
        hud.resize(width, height)
    }

    override fun show() {
        Gdx.input.inputProcessor = SwipeInputProcessor(this) { hud.fireButtonBounds() }
    }

    override fun pause() {}
    override fun resume() {}
    override fun hide() {}

    override fun dispose() {
        shapeRenderer.dispose()
        batch.dispose()
        hud.dispose()
    }
}
