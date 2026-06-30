# Frosty Maze

A minimalist ice-maze game built with Kotlin and LibGDX, inspired by the
classic "trap enemies in ice" arcade formula. Swipe to move, tap a single
button to fire ice and break it, collect all the fruit on each level while
dodging four kinds of creatures.

## Gameplay

- **Swipe** in any direction to move the player one tile at a time.
- **Tap the ice button** to fire a line of ice in the direction you're
  facing — it freezes empty tiles into walkable-but-breakable ice, or
  shatters an existing line of ice back to empty space.
- **Collect every fruit** on the level to advance; some fruit sit still,
  some flee from the player, and some teleport around the maze.
- **Avoid the creatures**: patrollers, wanderers, an ice-crusher that
  breaks through ice tiles, and a chaser that alternates between wandering
  and actively hunting the player.
- Each level has a time limit; running out of time or getting caught by a
  creature resets the current level.

## Architecture

### Core Classes

- **Player**: grid-based movement, facing direction, ice-firing logic
- **Enemy**: four AI archetypes (patrol, wanderer, ice-crusher, chaser)
- **Fruit**: stationary, fleeing, and teleporting collectible behaviors
- **Level**: tile grid (wall / ice / empty) with passability queries
- **LevelFactory**: builds levels from ASCII maps
- **GameState**: score and per-level countdown timer
- **GameScreen**: phase machine (title / playing / level complete / game
  over / caught / victory), update loop, and rendering
- **Hud**: on-screen score, timer, fruit tray, fire button, and overlays
- **SwipeInputProcessor**: converts touch gestures into swipe/tap/fire
  input events
- **BlankRunnerGame**: LibGDX `ApplicationAdapter` entry point

## Building & Running

This is a LibGDX multi-module Gradle project (`core` + `android`), built
with the Android Gradle Plugin — there is no desktop launcher module.

### Build the debug APK
```bash
gradle android:assembleDebug
```

The APK is also built automatically in the cloud via GitHub Actions on
every push to `main` (see `.github/workflows/build-apk.yml`), so a local
Android SDK setup isn't required to get an installable build.

## Project Structure

```
core/src/main/kotlin/com/blankrunner/
├── BlankRunnerGame.kt        # LibGDX entry point
├── GameScreen.kt             # Phase machine, update loop, rendering
├── Player.kt                 # Player entity
├── Enemy.kt                  # Enemy AI archetypes
├── Fruit.kt                  # Fruit behaviors
├── Level.kt                  # Tile grid
├── LevelFactory.kt           # ASCII level maps
├── GameState.kt              # Score + timer
├── Hud.kt                    # HUD and overlays
├── SwipeInputProcessor.kt    # Touch input
├── Direction.kt              # Movement directions
├── GamePhase.kt              # Game phase enum
└── TileType.kt                # Wall / ice / empty

android/src/main/kotlin/com/blankrunner/
└── AndroidLauncher.kt         # Android activity entry point
```
