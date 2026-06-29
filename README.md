# Blank Runner - Minimalist Platformer

A minimalist platformer game built with Kotlin and LibGDX.

## Features

- **Player Movement**: Left/Right arrow keys or A/D to move
- **Jumping**: Spacebar or W to jump  
- **Gravity**: Realistic gravity physics
- **Collision Detection**: Full platform collision system
- **Multiple Platforms**: Level with various platform heights
- **Pure Aesthetic**: White/black minimalist design, no external assets needed

## Architecture

### Core Classes

- **Player**: Handles character position, velocity, and state
- **Platform**: Represents static and dynamic platforms
- **PhysicsEngine**: Manages gravity and collision detection
- **InputHandler**: Processes keyboard input
- **GameScreen**: Main game loop and rendering
- **BlankRunnerGame**: LibGDX ApplicationListener entry point

## Building & Running

### Prerequisites
- Java 11+
- Gradle

### Build
```bash
gradle build
```

### Run
```bash
gradle run
```

### Distribution
Executable JAR: `build/libs/blank-runner-1.0-SNAPSHOT.jar`

## Controls

- **Move Left**: ← or A
- **Move Right**: → or D
- **Jump**: SPACE or W

## Game Mechanics

- Player spawns at position (100, 150)
- Gravity constant: -600 units/second²
- Jump power: 400 units/second
- Move speed: 150 units/second
- Platforms with collision detection and response

## Project Structure

```
src/main/kotlin/com/blankrunner/
├── DesktopLauncher.kt       # Entry point
├── BlankRunnerGame.kt        # Main game class
├── GameScreen.kt            # Game loop & rendering
├── Player.kt                # Player entity
├── Platform.kt              # Platform entity
├── PhysicsEngine.kt         # Physics simulation
└── InputHandler.kt          # Input processing
```
