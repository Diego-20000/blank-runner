package com.blankrunner

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.graphics.GL20

class BlankRunnerGame : ApplicationAdapter() {
    private lateinit var gameScreen: GameScreen

    override fun create() {
        gameScreen = GameScreen()
    }

    override fun render() {
        com.badlogic.gdx.Gdx.gl.glClearColor(1f, 1f, 1f, 1f)
        com.badlogic.gdx.Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        gameScreen.render(com.badlogic.gdx.Gdx.graphics.deltaTime)
    }

    override fun dispose() {
        gameScreen.dispose()
    }
}
