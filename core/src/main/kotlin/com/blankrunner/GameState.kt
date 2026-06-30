package com.blankrunner

class GameState {
    var score = 0
    var timeRemaining = LEVEL_TIME
        private set
    var timedOut = false
        private set

    companion object {
        const val LEVEL_TIME = 120f
    }

    /** Advances the countdown. Returns true the moment time runs out. */
    fun tick(deltaTime: Float): Boolean {
        if (timedOut) return false
        timeRemaining -= deltaTime
        if (timeRemaining <= 0f) {
            timeRemaining = 0f
            timedOut = true
            return true
        }
        return false
    }

    fun addScore(points: Int) {
        score += points
    }

    fun startLevelTimer() {
        timeRemaining = LEVEL_TIME
        timedOut = false
    }

    fun fullReset() {
        score = 0
        startLevelTimer()
    }
}
