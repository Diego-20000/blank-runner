package com.blankrunner

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx

class BlankRunnerGame : ApplicationAdapter() {
    private lateinit var gameScreen: GameScreen

    override fun create() {
        gameScreen = GameScreen()
        gameScreen.show()
    }

    override fun resize(width: Int, height: Int) {
        gameScreen.resize(width, height)
    }

    override fun render() {
        gameScreen.render(Gdx.graphics.deltaTime)
    }

    override fun dispose() {
        gameScreen.dispose()
    }
}
