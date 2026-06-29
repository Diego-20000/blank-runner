package com.blankrunner

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration

fun main() {
    val config = Lwjgl3ApplicationConfiguration().apply {
        setTitle("Blank Runner")
        setWindowedMode(800, 600)
        setForegroundFPS(60)
    }
    Lwjgl3Application(BlankRunnerGame(), config)
}
