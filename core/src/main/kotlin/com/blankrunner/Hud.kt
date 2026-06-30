package com.blankrunner

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle

class Hud {
    private val font = BitmapFont()
    private val camera = OrthographicCamera()
    private val fireButtonRadius = 55f
    private var screenWidth = 0f
    private var screenHeight = 0f

    init {
        font.color = Color.BLACK
        font.data.setScale(1.6f)
    }

    fun resize(width: Int, height: Int) {
        screenWidth = width.toFloat()
        screenHeight = height.toFloat()
        camera.setToOrtho(false, screenWidth, screenHeight)
        camera.update()
    }

    fun fireButtonBounds(): Rectangle {
        val cx = screenWidth - 80f
        val cy = 80f
        return Rectangle(cx - fireButtonRadius, cy - fireButtonRadius, fireButtonRadius * 2f, fireButtonRadius * 2f)
    }

    fun render(batch: SpriteBatch, shapeRenderer: ShapeRenderer, gameState: GameState, fruits: List<Fruit>) {
        shapeRenderer.projectionMatrix = camera.combined
        batch.projectionMatrix = camera.combined

        val buttonBounds = fireButtonBounds()
        val bx = buttonBounds.x + buttonBounds.width / 2f
        val by = buttonBounds.y + buttonBounds.height / 2f

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color(0.82f, 0.88f, 0.95f, 1f)
        shapeRenderer.circle(bx, by, fireButtonRadius)
        shapeRenderer.end()

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.BLACK
        shapeRenderer.circle(bx, by, fireButtonRadius)
        shapeRenderer.end()

        val trayY = 40f
        val spacing = 36f
        val trayStartX = screenWidth / 2f - (fruits.size - 1) * spacing / 2f
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        for ((i, fruit) in fruits.withIndex()) {
            shapeRenderer.color = colorFor(fruit.type, fruit.collected)
            shapeRenderer.circle(trayStartX + i * spacing, trayY, 14f)
        }
        shapeRenderer.end()

        batch.begin()
        font.draw(batch, "Score: ${gameState.score}", 20f, screenHeight - 20f)
        val minutes = (gameState.timeRemaining / 60).toInt()
        val seconds = (gameState.timeRemaining % 60).toInt()
        val timeText = String.format("%d:%02d", minutes, seconds)
        font.draw(batch, timeText, screenWidth / 2f - 20f, screenHeight - 20f)

        if (gameState.isLevelComplete) {
            font.draw(batch, "LEVEL COMPLETE!", screenWidth / 2f - 90f, screenHeight / 2f)
        } else if (gameState.isGameOver) {
            font.draw(batch, "GAME OVER - tap to retry", screenWidth / 2f - 140f, screenHeight / 2f)
        }
        batch.end()
    }

    private fun colorFor(type: FruitType, collected: Boolean): Color {
        if (collected) return Color(0.85f, 0.85f, 0.85f, 1f)
        return when (type) {
            FruitType.BERRY -> Color(0.85f, 0.2f, 0.25f, 1f)
            FruitType.GRAPE -> Color(0.55f, 0.3f, 0.75f, 1f)
            FruitType.MELON -> Color(0.35f, 0.7f, 0.35f, 1f)
        }
    }

    fun dispose() {
        font.dispose()
    }
}
