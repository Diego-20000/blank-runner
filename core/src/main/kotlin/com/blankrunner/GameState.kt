package com.blankrunner

class GameState {
    var score = 0
        private set
    var timeRemaining = 120f
        private set
    var isGameOver = false
        private set
    var isLevelComplete = false
        private set

    fun update(deltaTime: Float) {
        if (isGameOver || isLevelComplete) return
        timeRemaining -= deltaTime
        if (timeRemaining <= 0f) {
            timeRemaining = 0f
            isGameOver = true
        }
    }

    fun addScore(points: Int) {
        score += points
    }

    fun completeLevel() {
        isLevelComplete = true
    }

    fun fail() {
        isGameOver = true
    }

    fun reset() {
        score = 0
        timeRemaining = 120f
        isGameOver = false
        isLevelComplete = false
    }
}
