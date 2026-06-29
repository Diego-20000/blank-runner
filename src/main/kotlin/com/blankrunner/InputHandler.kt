package com.blankrunner

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input

class InputHandler {
    var isLeftPressed = false
    var isRightPressed = false
    var isJumpPressed = false

    fun update() {
        isLeftPressed = Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)
        isRightPressed = Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)
        isJumpPressed = Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.W)
    }
}
