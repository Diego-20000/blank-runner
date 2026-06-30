package com.blankrunner

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle

class Hud {
    private val font = BitmapFont()
    private val layout = GlyphLayout()
    private val camera = OrthographicCamera()
    private val fireButtonRadius = 58f
    private var screenWidth = 0f
    private var screenHeight = 0f

    init {
        font.color = Color.BLACK
    }

    fun resize(width: Int, height: Int) {
        screenWidth = width.toFloat()
        screenHeight = height.toFloat()
        camera.setToOrtho(false, screenWidth, screenHeight)
        camera.update()
    }

    fun fireButtonBounds(): Rectangle {
        val cx = screenWidth - 90f
        val cy = 90f
        return Rectangle(
            cx - fireButtonRadius, cy - fireButtonRadius,
            fireButtonRadius * 2f, fireButtonRadius * 2f
        )
    }

    private fun applyProjection(batch: SpriteBatch, shapeRenderer: ShapeRenderer) {
        shapeRenderer.projectionMatrix = camera.combined
        batch.projectionMatrix = camera.combined
    }

    fun renderPlaying(
        batch: SpriteBatch,
        shapeRenderer: ShapeRenderer,
        gameState: GameState,
        fruits: List<Fruit>,
        levelNumber: Int
    ) {
        applyProjection(batch, shapeRenderer)
        drawFireButton(shapeRenderer)
        drawFruitTray(shapeRenderer, fruits)
        drawTopBar(batch, gameState, levelNumber)
    }

    private fun drawFireButton(shapeRenderer: ShapeRenderer) {
        val b = fireButtonBounds()
        val cx = b.x + b.width / 2f
        val cy = b.y + b.height / 2f

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color(0.80f, 0.90f, 0.98f, 1f)
        shapeRenderer.circle(cx, cy, fireButtonRadius)
        shapeRenderer.end()

        // Snowflake glyph: three crossing spokes.
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color(0.30f, 0.55f, 0.80f, 1f)
        val r = fireButtonRadius * 0.55f
        for (i in 0 until 3) {
            val a = Math.toRadians((i * 60).toDouble())
            val ox = (Math.cos(a) * r).toFloat()
            val oy = (Math.sin(a) * r).toFloat()
            shapeRenderer.line(cx - ox, cy - oy, cx + ox, cy + oy)
        }
        shapeRenderer.end()

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color(0.35f, 0.55f, 0.72f, 1f)
        shapeRenderer.circle(cx, cy, fireButtonRadius)
        shapeRenderer.end()
    }

    private fun drawFruitTray(shapeRenderer: ShapeRenderer, fruits: List<Fruit>) {
        if (fruits.isEmpty()) return
        val trayY = 46f
        val spacing = 40f
        val startX = screenWidth / 2f - (fruits.size - 1) * spacing / 2f

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        for ((i, fruit) in fruits.withIndex()) {
            shapeRenderer.color = if (fruit.collected) {
                Color(0.86f, 0.88f, 0.90f, 1f)
            } else {
                fruitColor(fruit.type)
            }
            shapeRenderer.circle(startX + i * spacing, trayY, 15f)
        }
        shapeRenderer.end()

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color(0.45f, 0.47f, 0.50f, 1f)
        for (i in fruits.indices) {
            shapeRenderer.circle(startX + i * spacing, trayY, 15f)
        }
        shapeRenderer.end()
    }

    private fun drawTopBar(batch: SpriteBatch, gameState: GameState, levelNumber: Int) {
        batch.begin()
        font.color = Color.BLACK
        font.data.setScale(1.5f)
        font.draw(batch, "SCORE ${gameState.score}", 24f, screenHeight - 22f)

        val minutes = (gameState.timeRemaining / 60).toInt()
        val seconds = (gameState.timeRemaining % 60).toInt()
        val timeText = String.format("%d:%02d", minutes, seconds)
        layout.setText(font, timeText)
        font.draw(batch, timeText, screenWidth / 2f - layout.width / 2f, screenHeight - 22f)

        val levelText = "LEVEL $levelNumber"
        layout.setText(font, levelText)
        font.draw(batch, levelText, screenWidth - layout.width - 24f, screenHeight - 22f)
        batch.end()
    }

    fun renderOverlay(
        batch: SpriteBatch,
        shapeRenderer: ShapeRenderer,
        phase: GamePhase,
        levelNumber: Int,
        score: Int
    ) {
        applyProjection(batch, shapeRenderer)

        if (phase != GamePhase.TITLE) {
            com.badlogic.gdx.Gdx.gl.glEnable(GL20.GL_BLEND)
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
            shapeRenderer.color = Color(0.05f, 0.08f, 0.14f, 0.55f)
            shapeRenderer.rect(0f, 0f, screenWidth, screenHeight)
            shapeRenderer.end()
        }

        val (title, subtitle) = when (phase) {
            GamePhase.TITLE -> "FROSTY MAZE" to "Swipe to move  -  button to throw ice\nCollect all the fruit, dodge the creatures\n\nTap to play"
            GamePhase.LEVEL_COMPLETE -> "LEVEL $levelNumber CLEARED" to "Tap to continue"
            GamePhase.GAME_OVER -> "TIME'S UP" to "Tap to try this level again"
            GamePhase.VICTORY -> "YOU WIN!" to "Final score $score\n\nTap to return to title"
            GamePhase.PLAYING -> return
        }

        batch.begin()
        font.color = if (phase == GamePhase.TITLE) Color(0.18f, 0.40f, 0.62f, 1f) else Color.WHITE

        font.data.setScale(3.2f)
        layout.setText(font, title)
        font.draw(batch, title, screenWidth / 2f - layout.width / 2f, screenHeight * 0.66f)

        font.data.setScale(1.4f)
        font.color = if (phase == GamePhase.TITLE) Color(0.30f, 0.35f, 0.42f, 1f) else Color(0.90f, 0.93f, 0.97f, 1f)
        layout.setText(font, subtitle)
        font.draw(
            batch, subtitle,
            screenWidth / 2f - layout.width / 2f,
            screenHeight * 0.66f - 70f,
            layout.width, com.badlogic.gdx.utils.Align.center, false
        )
        batch.end()

        font.data.setScale(1f)
    }

    private fun fruitColor(type: FruitType): Color = when (type) {
        FruitType.BERRY -> Color(0.85f, 0.20f, 0.28f, 1f)
        FruitType.GRAPE -> Color(0.55f, 0.30f, 0.78f, 1f)
        FruitType.MELON -> Color(0.36f, 0.72f, 0.38f, 1f)
    }

    fun dispose() {
        font.dispose()
    }
}
