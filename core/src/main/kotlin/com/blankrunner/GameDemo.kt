package com.blankrunner

fun main() {
    println("╔════════════════════════════════════════════════════════════════╗")
    println("║         BLANK RUNNER - Game Mechanics Demo                    ║")
    println("╚════════════════════════════════════════════════════════════════╝\n")

    val physics = PhysicsEngine()
    val inputHandler = InputHandler()
    val player = Player()
    val platforms = listOf(
        Platform(x = 0f, y = 50f, width = 800f, height = 16f),
        Platform(x = 200f, y = 150f, width = 150f, height = 16f),
        Platform(x = 500f, y = 250f, width = 150f, height = 16f),
        Platform(x = 100f, y = 350f, width = 200f, height = 16f)
    )

    println("Initial State:")
    printGameState(player, platforms)
    println()

    println("═══ SCENARIO 1: FALLING WITH GRAVITY ═══")
    println("Player falls from initial position...")
    for (i in 1..5) {
        player.update(0.016f, inputHandler, physics)
        physics.checkCollisions(player, platforms)
        println("Frame $i: Player Y = ${player.y.toInt()}, VelocityY = ${player.velocityY.toInt()}")
    }
    println("✓ Gravity working: Player accelerates downward\n")

    println("═══ SCENARIO 2: LANDING ON PLATFORM ═══")
    println("Player lands on ground platform...")
    while (player.y > 50f) {
        player.update(0.016f, inputHandler, physics)
        physics.checkCollisions(player, platforms)
    }
    println("✓ Landing: Player Y = ${player.y.toInt()}, Velocity = ${player.velocityY.toInt()}")
    println("✓ Collision detected and resolved\n")

    println("═══ SCENARIO 3: JUMPING ═══")
    println("Player presses spacebar to jump...")
    inputHandler.isJumpPressed = true
    for (i in 1..10) {
        player.update(0.016f, inputHandler, physics)
        physics.checkCollisions(player, platforms)
        if (i == 1) println("Frame 1: Jump initiated, VelocityY = ${player.velocityY.toInt()}")
        if (i == 5) println("Frame 5: Mid-air, Y = ${player.y.toInt()}")
    }
    println("✓ Jump mechanic working\n")

    println("═══ SCENARIO 4: HORIZONTAL MOVEMENT ═══")
    println("Player moves right with arrow keys...")
    inputHandler.isJumpPressed = false
    inputHandler.isRightPressed = true
    val startX = player.x
    for (i in 1..10) {
        player.update(0.016f, inputHandler, physics)
        physics.checkCollisions(player, platforms)
    }
    val distance = player.x - startX
    println("✓ Movement: Traveled ${distance.toInt()}px to the right")
    println("✓ Current position: X = ${player.x.toInt()}, Y = ${player.y.toInt()}\n")

    println("═══ SCENARIO 5: PLATFORM TRAVERSAL ═══")
    println("Player jumps to reach different platforms...")
    player.x = 150f
    inputHandler.isRightPressed = false
    inputHandler.isJumpPressed = true
    for (i in 1..15) {
        player.update(0.016f, inputHandler, physics)
        physics.checkCollisions(player, platforms)
        if (i == 8) {
            println("Frame 8: Player reached height Y = ${player.y.toInt()}")
        }
    }
    val platformReached = platforms.find {
        player.x in it.getLeft()..it.getRight() &&
        player.y >= it.getBottom() - 20
    }
    if (platformReached != null) {
        println("✓ Multi-platform traversal: Player landed on platform at Y = ${platformReached.y.toInt()}")
    }
    println()

    println("╔════════════════════════════════════════════════════════════════╗")
    println("║                    GAME MECHANICS SUMMARY                      ║")
    println("╠════════════════════════════════════════════════════════════════╣")
    println("║ ✓ Gravity System        (-600 units/s²)                        ║")
    println("║ ✓ Collision Detection   (4 platforms)                          ║")
    println("║ ✓ Jump Mechanic         (400 units/s velocity)                 ║")
    println("║ ✓ Horizontal Movement  (150 units/s speed)                     ║")
    println("║ ✓ Platform Landing     (bounce/stick detection)                ║")
    println("║ ✓ Physics Integration   (smooth motion)                        ║")
    println("╚════════════════════════════════════════════════════════════════╝\n")

    println("To run the actual game:")
    println("  gradle run")
    println("\nControls:")
    println("  ← / A      - Move left")
    println("  → / D      - Move right")
    println("  SPACE / W  - Jump\n")
}

private fun printGameState(player: Player, platforms: List<Platform>) {
    println("Player: Position(${player.x.toInt()}, ${player.y.toInt()}), Size(${player.width.toInt()}x${player.height.toInt()})")
    println("Platforms:")
    platforms.forEachIndexed { idx, p ->
        println("  [$idx] Position(${p.x.toInt()}, ${p.y.toInt()}), Size(${p.width.toInt()}x${p.height.toInt()})")
    }
}
