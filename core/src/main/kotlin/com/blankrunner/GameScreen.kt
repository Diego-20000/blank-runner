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

    private var levelData = LevelFactory.level1()
    private var level = levelData.level
    private var fruits = levelData.fruits
    private var enemies = levelData.enemies
    private val player = Player(levelData.playerStartX, levelData.playerStartY)

    private val tileSize = level.tileSize

    init {
        val worldWidth = LevelFactory.COLS * tileSize
        val worldHeight = LevelFactory.ROWS * tileSize
        camera.position.set(worldWidth / 2f, worldHeight / 2f, 0f)
        viewport = FitViewport(worldWidth, worldHeight, camera)
        Gdx.input.inputProcessor = SwipeInputProcessor(this) { hud.fireButtonBounds() }
    }

    private fun resetLevel(keepScore: Boolean) {
        levelData = LevelFactory.level1()
        level = levelData.level
        fruits = levelData.fruits
        enemies = levelData.enemies
        player.setStart(levelData.playerStartX, levelData.playerStartY)

        val scoreToKeep = if (keepScore) gameState.score else 0
        gameState.reset()
        if (scoreToKeep > 0) gameState.addScore(scoreToKeep)
    }

    override fun onSwipe(direction: Direction) {
        if (gameState.isGameOver || gameState.isLevelComplete) return
        player.trySetDirection(direction, level)
    }

    override fun onFireButton() {
        if (gameState.isGameOver) {
            resetLevel(keepScore = false)
        } else if (gameState.isLevelComplete) {
            resetLevel(keepScore = false)
        } else {
            player.fireIce(level)
        }
    }

    override fun render(deltaTime: Float) {
        update(deltaTime)
        draw()
    }

    private fun update(deltaTime: Float) {
        gameState.update(deltaTime)
        if (gameState.isGameOver || gameState.isLevelComplete) return

        player.update(deltaTime, level)
        for (enemy in enemies) {
            enemy.update(deltaTime, level, player.gridX, player.gridY)
        }
        for (fruit in fruits) {
            fruit.update(deltaTime, level, player.gridX, player.gridY)
        }

        for (fruit in fruits) {
            if (!fruit.collected && fruit.gridX == player.gridX && fruit.gridY == player.gridY) {
                fruit.collected = true
                gameState.addScore(10)
            }
        }

        if (fruits.all { it.collected }) {
            gameState.completeLevel()
        }

        if (enemies.any { it.gridX == player.gridX && it.gridY == player.gridY }) {
            resetLevel(keepScore = true)
        }
    }

    private fun draw() {
        Gdx.gl.glClearColor(0.96f, 0.97f, 1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        viewport.apply()
        camera.update()
        shapeRenderer.projectionMatrix = camera.combined

        drawTiles()
        drawFruits()
        drawEnemies()
        drawPlayer()

        hud.render(batch, shapeRenderer, gameState, fruits)
    }

    private fun drawTiles() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        for (x in 0 until level.cols) {
            for (y in 0 until level.rows) {
                val type = level.tileAt(x, y)
                if (type == TileType.EMPTY) continue
                shapeRenderer.color = if (type == TileType.WALL) {
                    Color(0.25f, 0.27f, 0.32f, 1f)
                } else {
                    Color(0.65f, 0.82f, 0.95f, 1f)
                }
                shapeRenderer.rect(level.worldX(x), level.worldY(y), tileSize, tileSize)
            }
        }
        shapeRenderer.end()

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color(0.8f, 0.85f, 0.9f, 1f)
        for (x in 0..level.cols) {
            shapeRenderer.line(x * tileSize, 0f, x * tileSize, level.rows * tileSize)
        }
        for (y in 0..level.rows) {
            shapeRenderer.line(0f, y * tileSize, level.cols * tileSize, y * tileSize)
        }
        shapeRenderer.end()
    }

    private fun drawFruits() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        for (fruit in fruits) {
            if (fruit.collected) continue
            shapeRenderer.color = when (fruit.type) {
                FruitType.BERRY -> Color(0.85f, 0.2f, 0.25f, 1f)
                FruitType.GRAPE -> Color(0.55f, 0.3f, 0.75f, 1f)
                FruitType.MELON -> Color(0.35f, 0.7f, 0.35f, 1f)
            }
            val cx = level.worldX(fruit.gridX) + tileSize / 2f
            val cy = level.worldY(fruit.gridY) + tileSize / 2f
            shapeRenderer.circle(cx, cy, tileSize * 0.28f)
        }
        shapeRenderer.end()
    }

    private fun drawEnemies() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        for (enemy in enemies) {
            shapeRenderer.color = when (enemy.type) {
                EnemyType.PATROL -> Color(0.95f, 0.6f, 0.15f, 1f)
                EnemyType.WANDERER -> Color(0.95f, 0.85f, 0.2f, 1f)
                EnemyType.ICE_CRUSHER -> Color(0.4f, 0.45f, 0.95f, 1f)
                EnemyType.CHASER -> Color(0.9f, 0.25f, 0.5f, 1f)
            }
            val pad = tileSize * 0.12f
            shapeRenderer.rect(
                level.worldX(enemy.gridX) + pad,
                level.worldY(enemy.gridY) + pad,
                tileSize - pad * 2f,
                tileSize - pad * 2f
            )
        }
        shapeRenderer.end()
    }

    private fun drawPlayer() {
        val px = player.visualX() * tileSize
        val py = player.visualY() * tileSize
        val pad = tileSize * 0.1f

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color.WHITE
        shapeRenderer.rect(px + pad, py + pad, tileSize - pad * 2f, tileSize - pad * 2f)
        shapeRenderer.end()

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.BLACK
        shapeRenderer.rect(px + pad, py + pad, tileSize - pad * 2f, tileSize - pad * 2f)
        shapeRenderer.end()

        val cx = px + tileSize / 2f
        val cy = py + tileSize / 2f
        val faceX = cx + player.facing.dx * tileSize * 0.3f
        val faceY = cy + player.facing.dy * tileSize * 0.3f
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color.BLACK
        shapeRenderer.circle(faceX, faceY, tileSize * 0.08f)
        shapeRenderer.end()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
        hud.resize(width, height)
    }

    override fun pause() {}

    override fun resume() {}

    override fun hide() {}

    override fun show() {}

    override fun dispose() {
        shapeRenderer.dispose()
        batch.dispose()
        hud.dispose()
    }
}
