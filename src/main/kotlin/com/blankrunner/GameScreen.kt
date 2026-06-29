package com.blankrunner

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer

class GameScreen : Screen {
    private val player = Player()
    private val physics = PhysicsEngine()
    private val inputHandler = InputHandler()
    private val shapeRenderer = ShapeRenderer()

    private val platforms = mutableListOf<Platform>()

    init {
        setupPlatforms()
    }

    private fun setupPlatforms() {
        platforms.add(Platform(x = 0f, y = 50f, width = 800f))
        platforms.add(Platform(x = 200f, y = 150f, width = 150f))
        platforms.add(Platform(x = 500f, y = 250f, width = 150f))
        platforms.add(Platform(x = 100f, y = 350f, width = 200f))
    }

    override fun render(deltaTime: Float) {
        clearScreen()

        player.update(deltaTime, inputHandler, physics)
        physics.checkCollisions(player, platforms)

        for (platform in platforms) {
            platform.update(deltaTime)
        }

        render(platforms)
        renderPlayer()
    }

    private fun clearScreen() {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    }

    private fun render(platforms: List<Platform>) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.setColor(1f, 1f, 1f, 1f)
        for (platform in platforms) {
            shapeRenderer.rect(platform.x, platform.y, platform.width, platform.height)
        }
        shapeRenderer.end()

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.setColor(0f, 0f, 0f, 1f)
        for (platform in platforms) {
            shapeRenderer.rect(platform.x, platform.y, platform.width, platform.height)
        }
        shapeRenderer.end()
    }

    private fun renderPlayer() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.setColor(1f, 1f, 1f, 1f)
        shapeRenderer.rect(player.x, player.y, player.width, player.height)
        shapeRenderer.end()

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.setColor(0f, 0f, 0f, 1f)
        shapeRenderer.rect(player.x, player.y, player.width, player.height)
        shapeRenderer.end()
    }

    override fun resize(width: Int, height: Int) {}

    override fun pause() {}

    override fun resume() {}

    override fun hide() {}

    override fun show() {}

    override fun dispose() {
        shapeRenderer.dispose()
    }
}
