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

    private fun ellipseCentered(cx: Float, cy: Float, halfW: Float, halfH: Float) {
        shapeRenderer.ellipse(cx - halfW, cy - halfH, halfW * 2f, halfH * 2f)
    }

    private fun diamond(cx: Float, cy: Float, halfW: Float, halfH: Float) {
        shapeRenderer.triangle(cx - halfW, cy, cx, cy + halfH, cx + halfW, cy)
        shapeRenderer.triangle(cx - halfW, cy, cx, cy - halfH, cx + halfW, cy)
    }

    private fun drawWallTile(wx: Float, wy: Float, gy: Int) {
        shapeRenderer.color = Color(0.18f, 0.21f, 0.27f, 1f)
        shapeRenderer.rect(wx, wy, tileSize, tileSize)
        shapeRenderer.color = Color(0.27f, 0.31f, 0.39f, 1f)
        shapeRenderer.rect(wx + tileSize * 0.05f, wy + tileSize * 0.05f, tileSize * 0.90f, tileSize * 0.90f)
        shapeRenderer.color = Color(0.37f, 0.42f, 0.51f, 1f)
        shapeRenderer.rect(wx + tileSize * 0.05f, wy + tileSize * 0.72f, tileSize * 0.90f, tileSize * 0.23f)
        // staggered mortar seams give the wall block a real brick silhouette
        shapeRenderer.color = Color(0.18f, 0.21f, 0.27f, 1f)
        val seamX = if (gy % 2 == 0) wx + tileSize * 0.5f else wx + tileSize * 0.22f
        shapeRenderer.rect(seamX - tileSize * 0.015f, wy + tileSize * 0.05f, tileSize * 0.03f, tileSize * 0.67f)
        shapeRenderer.rect(wx + tileSize * 0.05f, wy + tileSize * 0.37f, tileSize * 0.90f, tileSize * 0.025f)
    }

    private fun drawIceTile(wx: Float, wy: Float, gx: Int, gy: Int) {
        shapeRenderer.color = Color(0.58f, 0.80f, 0.95f, 1f)
        shapeRenderer.rect(wx, wy, tileSize, tileSize)
        shapeRenderer.color = Color(0.74f, 0.89f, 0.99f, 1f)
        if ((gx + gy) % 2 == 0) {
            shapeRenderer.triangle(
                wx + tileSize * 0.10f, wy + tileSize * 0.15f,
                wx + tileSize * 0.55f, wy + tileSize * 0.85f,
                wx + tileSize * 0.90f, wy + tileSize * 0.35f
            )
        } else {
            shapeRenderer.triangle(
                wx + tileSize * 0.90f, wy + tileSize * 0.85f,
                wx + tileSize * 0.45f, wy + tileSize * 0.15f,
                wx + tileSize * 0.10f, wy + tileSize * 0.65f
            )
        }
        shapeRenderer.color = Color(0.90f, 0.97f, 1f, 1f)
        diamond(wx + tileSize * 0.76f, wy + tileSize * 0.78f, tileSize * 0.07f, tileSize * 0.07f)
        shapeRenderer.color = Color(0.45f, 0.68f, 0.86f, 1f)
        shapeRenderer.rect(wx, wy, tileSize, tileSize * 0.05f)
    }

    private fun drawTiles() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        for (x in 0 until level.cols) {
            for (y in 0 until level.rows) {
                val wx = level.worldX(x)
                val wy = level.worldY(y)
                when (level.tileAt(x, y)) {
                    TileType.WALL -> drawWallTile(wx, wy, y)
                    TileType.ICE -> drawIceTile(wx, wy, x, y)
                    TileType.EMPTY -> {}
                }
            }
        }
        shapeRenderer.end()

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color(0.84f, 0.89f, 0.94f, 1f)
        for (x in 0 until level.cols) {
            for (y in 0 until level.rows) {
                if (level.tileAt(x, y) == TileType.EMPTY) {
                    shapeRenderer.rect(level.worldX(x), level.worldY(y), tileSize, tileSize)
                }
            }
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
            shapeRenderer.color = Color(0f, 0f, 0f, 0.15f)
            ellipseCentered(cx, cy - r * 0.95f, r * 0.85f, r * 0.22f)

            shapeRenderer.color = Color(0.30f, 0.55f, 0.30f, 1f)
            shapeRenderer.rect(cx - tileSize * 0.025f, cy + r * 0.55f, tileSize * 0.05f, tileSize * 0.16f)
            shapeRenderer.color = Color(0.42f, 0.70f, 0.40f, 1f)
            shapeRenderer.triangle(
                cx + tileSize * 0.025f, cy + r * 0.78f,
                cx + tileSize * 0.13f, cy + r * 0.95f,
                cx + tileSize * 0.04f, cy + r * 1.02f
            )

            shapeRenderer.color = fruitColor(fruit.type)
            shapeRenderer.circle(cx, cy, r)
            shapeRenderer.color = Color(1f, 1f, 1f, 0.6f)
            shapeRenderer.circle(cx - r * 0.35f, cy + r * 0.35f, r * 0.22f)
            shapeRenderer.color = Color(0.1f, 0.1f, 0.15f, 0.16f)
            ellipseCentered(cx, cy - r * 0.4f, r * 0.7f, r * 0.32f)
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

    /** Each archetype gets a distinct silhouette so they read apart at a glance. */
    private fun drawEnemies() {
        for (enemy in enemies) {
            val cx = enemy.renderX * tileSize + tileSize / 2f
            val cy = enemy.renderY * tileSize + tileSize / 2f
            val r = tileSize * 0.34f
            val color = enemyColor(enemy.type)
            val shade = color.cpy().mul(0.76f, 0.76f, 0.76f, 1f)

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
            shapeRenderer.color = Color(0f, 0f, 0f, 0.18f)
            ellipseCentered(cx, cy - r * 0.95f, r * 0.85f, r * 0.24f)

            when (enemy.type) {
                EnemyType.PATROL -> {
                    shapeRenderer.color = shade
                    shapeRenderer.triangle(cx - r * 0.55f, cy + r * 0.35f, cx - r * 0.85f, cy + r * 1.15f, cx - r * 0.1f, cy + r * 0.75f)
                    shapeRenderer.triangle(cx + r * 0.55f, cy + r * 0.35f, cx + r * 0.85f, cy + r * 1.15f, cx + r * 0.1f, cy + r * 0.75f)
                    shapeRenderer.color = color
                    shapeRenderer.circle(cx, cy, r)
                    shapeRenderer.color = shade
                    shapeRenderer.triangle(cx - r * 0.18f, cy - r * 0.5f, cx + r * 0.18f, cy - r * 0.5f, cx, cy - r * 0.95f)
                }
                EnemyType.WANDERER -> {
                    shapeRenderer.color = color
                    shapeRenderer.circle(cx, cy, r)
                    shapeRenderer.rect(cx - r * 0.05f, cy + r * 0.85f, r * 0.1f, r * 0.45f)
                    shapeRenderer.circle(cx, cy + r * 1.35f, r * 0.16f)
                    shapeRenderer.color = shade
                    shapeRenderer.triangle(cx - r * 0.95f, cy - r * 0.1f, cx - r * 0.55f, cy - r * 0.65f, cx - r * 0.25f, cy - r * 0.05f)
                    shapeRenderer.triangle(cx + r * 0.95f, cy - r * 0.1f, cx + r * 0.55f, cy - r * 0.65f, cx + r * 0.25f, cy - r * 0.05f)
                }
                EnemyType.ICE_CRUSHER -> {
                    shapeRenderer.color = shade
                    diamond(cx, cy, r * 1.08f, r * 1.08f)
                    shapeRenderer.color = color
                    diamond(cx, cy, r * 0.9f, r * 0.9f)
                    shapeRenderer.color = Color(0.78f, 0.84f, 1f, 1f)
                    shapeRenderer.triangle(cx - r * 0.3f, cy + r * 0.2f, cx, cy + r * 0.6f, cx + r * 0.1f, cy + r * 0.1f)
                }
                EnemyType.CHASER -> {
                    shapeRenderer.color = shade
                    shapeRenderer.triangle(cx - r * 0.65f, cy + r * 0.5f, cx - r * 0.95f, cy + r * 1.25f, cx - r * 0.2f, cy + r * 0.85f)
                    shapeRenderer.triangle(cx + r * 0.65f, cy + r * 0.5f, cx + r * 0.95f, cy + r * 1.25f, cx + r * 0.2f, cy + r * 0.85f)
                    shapeRenderer.color = color
                    shapeRenderer.circle(cx, cy, r)
                    shapeRenderer.color = Color(0.98f, 0.98f, 1f, 1f)
                    shapeRenderer.triangle(cx - r * 0.12f, cy - r * 0.75f, cx + r * 0.12f, cy - r * 0.75f, cx, cy - r * 1.05f)
                }
            }

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
            if (enemy.type == EnemyType.ICE_CRUSHER) {
                shapeRenderer.line(cx - r * 0.9f, cy, cx, cy + r * 0.9f)
                shapeRenderer.line(cx, cy + r * 0.9f, cx + r * 0.9f, cy)
                shapeRenderer.line(cx + r * 0.9f, cy, cx, cy - r * 0.9f)
                shapeRenderer.line(cx, cy - r * 0.9f, cx - r * 0.9f, cy)
            } else {
                shapeRenderer.circle(cx, cy, r)
            }
            shapeRenderer.end()
        }
    }

    private fun drawPlayer() {
        val cx = player.visualX() * tileSize + tileSize / 2f
        val cy = player.visualY() * tileSize + tileSize / 2f
        val r = tileSize * 0.34f

        // Eyes, snout and tail all read toward the facing direction.
        val fdx = player.facing.dx.toFloat()
        val fdy = player.facing.dy.toFloat()
        val perpX = -fdy
        val perpY = fdx

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color(0f, 0f, 0f, 0.18f)
        ellipseCentered(cx, cy - r * 0.95f, r * 0.85f, r * 0.24f)

        shapeRenderer.color = Color(0.88f, 0.91f, 0.97f, 1f)
        shapeRenderer.triangle(
            cx - fdx * r * 0.85f - perpX * r * 0.3f, cy - fdy * r * 0.85f - perpY * r * 0.3f,
            cx - fdx * r * 0.85f + perpX * r * 0.3f, cy - fdy * r * 0.85f + perpY * r * 0.3f,
            cx - fdx * r * 1.5f, cy - fdy * r * 1.5f
        )

        shapeRenderer.color = Color(0.93f, 0.94f, 0.99f, 1f)
        shapeRenderer.circle(cx - r * 0.55f, cy + r * 0.85f, r * 0.3f)
        shapeRenderer.circle(cx + r * 0.55f, cy + r * 0.85f, r * 0.3f)
        shapeRenderer.color = Color(0.62f, 0.82f, 0.96f, 1f)
        shapeRenderer.circle(cx - r * 0.55f, cy + r * 0.85f, r * 0.14f)
        shapeRenderer.circle(cx + r * 0.55f, cy + r * 0.85f, r * 0.14f)

        shapeRenderer.color = Color(0.62f, 0.82f, 0.96f, 1f)
        ellipseCentered(cx - r * 0.42f, cy - r * 0.85f, r * 0.22f, r * 0.16f)
        ellipseCentered(cx + r * 0.42f, cy - r * 0.85f, r * 0.22f, r * 0.16f)

        shapeRenderer.color = Color(0.98f, 0.98f, 1f, 1f)
        shapeRenderer.circle(cx, cy, r)
        shapeRenderer.color = Color(0.62f, 0.82f, 0.96f, 1f)
        shapeRenderer.circle(cx, cy - r * 0.1f, r * 0.7f)
        shapeRenderer.color = Color(0.98f, 0.98f, 1f, 1f)
        shapeRenderer.circle(cx + fdx * r * 0.7f, cy + fdy * r * 0.7f, r * 0.32f)
        shapeRenderer.end()

        val eyeFwd = r * 0.18f
        val eyeSep = r * 0.34f
        val ex = cx + fdx * eyeFwd
        val ey = cy + fdy * eyeFwd + r * 0.12f

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color.WHITE
        shapeRenderer.circle(ex + perpX * eyeSep, ey + perpY * eyeSep, r * 0.2f)
        shapeRenderer.circle(ex - perpX * eyeSep, ey - perpY * eyeSep, r * 0.2f)
        shapeRenderer.color = Color(0.12f, 0.12f, 0.16f, 1f)
        shapeRenderer.circle(ex + perpX * eyeSep, ey + perpY * eyeSep, r * 0.12f)
        shapeRenderer.circle(ex - perpX * eyeSep, ey - perpY * eyeSep, r * 0.12f)
        shapeRenderer.color = Color(0.15f, 0.15f, 0.18f, 1f)
        shapeRenderer.circle(cx + fdx * r * 0.95f, cy + fdy * r * 0.95f, r * 0.1f)
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
