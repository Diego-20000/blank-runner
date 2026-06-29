# Blank Runner - Game Mechanics & Cinematics

## Physics System

### Gravity
```
Frame 0:  Velocity Y = 0
Frame 1:  Velocity Y = -9.6   (falling)
Frame 2:  Velocity Y = -19.2  (accelerating)
Frame 3:  Velocity Y = -28.8  (faster fall)
...
Gravity: -600 units/second²
```

## Player Movement Scenarios

### 1. **Free Fall** (No Platforms Below)
```
Time 0ms: ▌ (Y: 400, VY: 0)
Time 16ms: ▌ (Y: 390, VY: -9.6)
Time 32ms: ▌ (Y: 375, VY: -19.2)
Time 48ms: ▌ (Y: 355, VY: -28.8)
...
```

### 2. **Jump from Platform**
```
BEFORE JUMP:
Ground ═════════════════════════════
Player ▌ (standing)

JUMP PRESSED (VY = +400):
       ▌ (Y: 66)
       ▌ (Y: 82)
       ▌ (Y: 91)  ← Peak height
       ▌ (Y: 82)
Ground ═════════════════════════════

FALLING BACK DOWN:
       ▌
       ▌
Ground ═════════════════════════════ ✓ Landing
```

### 3. **Horizontal Movement** (Left/Right)
```
WITHOUT INPUT:
X = 100 → X = 100 (static)

WITH RIGHT ARROW (150 units/s):
X = 100 → X = 102.4 → X = 104.8 → X = 107.2 → ...

WITH LEFT + JUMP:
Moving Left AND Falling
      ▌
    ▌
  ▌
▌ ← Landing on lower platform
```

## Level Layout

```
         Platform 3 (Y: 250)
            ┌─────────┐
            │    ▌    │
            └─────────┘

  Platform 2 (Y: 150)      Platform 4 (Y: 350)
  ┌──────────┐              ┌──────────┐
  │   ▌      │              │    ▌     │
  └──────────┘              └──────────┘

╔══════════════════════════════════════════════════════════════════╗
║ GROUND PLATFORM (Y: 50) - Base level                            ║
╚══════════════════════════════════════════════════════════════════╝

┌─────────────────────────────────────────────────────────────────┐
│ Screen: 800x600px                                               │
└─────────────────────────────────────────────────────────────────┘
```

## Collision Response

### Landing on Platform (from above)
```
BEFORE:                    AFTER:
▌ (VY: -250)      →        ▌ (VY: 0)
─────────────              ─────────────
```

### Hitting Head (from below)
```
BEFORE:                    AFTER:
─────────────              ─────────────
▌ (VY: +400)      →        ▌ (VY: 0)
```

### Side Collision
```
BEFORE:                    AFTER:
  ▌ │                        │ ▌
  ──→ WALL        →        WALL ←
```

## Frame-by-Frame Example: Jump Sequence

```
FRAME 0 (t=0ms):
Action: SPACEBAR PRESSED + RIGHT ARROW
Status: ▌ ON GROUND
Output: Jump initiated, VY = +400

FRAME 1-5 (t=16-80ms):
Status: ▌ ASCENDING
Position: Y decreasing (going up)
Velocity: Decreasing (gravity fighting jump)
Position: X increasing (moving right)

FRAME 8 (t=128ms):
Status: ▌ AT PEAK
Velocity: VY ≈ 0
Position: Maximum height

FRAME 9-15 (t=144-240ms):
Status: ▌ DESCENDING
Velocity: Increasing (negative, falling)
Position: Y increasing (coming down)

FRAME 16 (t=256ms):
Status: ▌ LANDING
Collision: Detected platform below
Response: VY = 0, Y = platform_top
```

## Input Processing

### Keyboard Input Detection
```
LEFT ARROW / A          → isLeftPressed = true
RIGHT ARROW / D         → isRightPressed = true
SPACEBAR / W            → isJumpPressed = true (only on land)

Priority:
1. Jump only works when NOT jumping
2. Left + Right = stationary (cancel out)
3. Single direction = move that way
```

### Movement Calculation
```
if (isLeftPressed && !isRightPressed):
    velocityX = -150

if (isRightPressed && !isLeftPressed):
    velocityX = +150

else:
    velocityX = 0  // Decelerate instantly

if (isJumpPressed && !isJumping):
    velocityY = +400
    isJumping = true

// Apply gravity every frame
velocityY += gravity * deltaTime
velocityY += -600 * 0.016 = -9.6 per frame
```

## Performance Metrics

- **Frame Rate**: 60 FPS (16.67ms per frame)
- **Gravity**: -600 units/s² (9.6 per frame)
- **Max Fall Speed**: Unlimited (but typically 300+ units/s)
- **Jump Height**: ~27 pixels max
- **Jump Duration**: ~1.3 seconds (up + down)
- **Move Speed**: 150 units/s (2.4 pixels/frame)
- **World Size**: 800x600 pixels

## Collision Detection Algorithm

```kotlin
// Check if player overlaps with platform
for each platform:
    if player.right > platform.left AND
       player.left < platform.right AND
       player.top > platform.bottom AND
       player.bottom < platform.top:
        HIT! → Resolve collision
```

## Collision Resolution

```kotlin
// Find which side was hit (minimal overlap)
val overlapTop = player.bottom - platform.top
val overlapBottom = platform.bottom - player.top
val overlapLeft = player.right - platform.left
val overlapRight = platform.right - player.left

// Resolve based on smallest overlap
when (minimum overlap):
    top    → player lands on platform, VY = 0
    bottom → player bonks head, VY = 0
    left   → player pushed left
    right  → player pushed right
```

## Win/Loss Conditions (Future)

```
WON:  Player reaches goal/exit
LOST: Player falls below Y = 0
      Player runs into spike/hazard
```

## Visual Rendering

### Colors (White/Black Theme)
```
Background:   White (1, 1, 1, 1)
Player:       White square with black outline
Platforms:    White rectangles with black outline
```

### Layer Order
1. Background (clear to white)
2. Platforms (render white, then outline)
3. Player (render white, then outline)

---

**Project Status**: ✅ Core mechanics complete and working
**Build Status**: ✅ Builds successfully
**Runtime Status**: ✅ Ready for GUI display
