# Development Workflow - Blank Runner

## TL;DR - Rápido

```bash
# Setup (una sola vez)
# Sigue: ../QUICK_START.md

# Desarrollo iterativo (después)
buildinstall

# ✓ APK compilado e instalado en 2 minutos
```

---

## Estructura del Proyecto

```
blank-runner/
├── core/                           # Lógica del juego (compartida)
│   └── src/main/kotlin/com/blankrunner/
│       ├── Player.kt              # Movimiento del jugador
│       ├── Platform.kt            # Plataformas
│       ├── PhysicsEngine.kt       # Física y colisiones
│       ├── InputHandler.kt        # Input táctil + teclado
│       ├── GameScreen.kt          # Rendering
│       └── BlankRunnerGame.kt     # Main game class
│
└── android/                        # App Android
    ├── src/main/kotlin/com/blankrunner/
    │   └── AndroidLauncher.kt     # Entry point Android
    ├── src/main/AndroidManifest.xml
    └── build.gradle
```

---

## Editar Código

### Archivo: `Player.kt`
```kotlin
// core/src/main/kotlin/com/blankrunner/Player.kt
class Player {
    // Velocidad de movimiento
    private val moveSpeed = 150f  // ← Cambiar aquí para más/menos velocidad
    
    // Poder de salto
    private val jumpPower = 400f  // ← Cambiar aquí para saltos más altos
}
```

### Archivo: `PhysicsEngine.kt`
```kotlin
// core/src/main/kotlin/com/blankrunner/PhysicsEngine.kt
class PhysicsEngine {
    private val gravity = -600f  // ← Más negativo = cae más rápido
}
```

### Archivo: `GameScreen.kt`
```kotlin
// core/src/main/kotlin/com/blankrunner/GameScreen.kt
private fun setupPlatforms() {
    platforms.add(Platform(x = 0f, y = 50f, width = 800f))     // Plataforma base
    platforms.add(Platform(x = 200f, y = 150f, width = 150f))  // Agregadas aquí
    // ↑ Editar para cambiar niveles
}
```

---

## Compilar y Probar

### 1. Edit Code
```bash
vim core/src/main/kotlin/com/blankrunner/Player.kt
# o usar tu editor favorito
```

### 2. Compilar + Instalar (ONE COMMAND)
```bash
# Alias (si lo configuraste):
buildinstall

# O manual:
gradle android:assembleDebug && \
adb install -r android/build/outputs/apk/debug/android-debug.apk && \
adb shell am start -n com.blankrunner/.AndroidLauncher
```

### 3. Ver en Dispositivo
- La app se abre automáticamente
- Prueba los cambios

### 4. Ver Logs
```bash
adb logcat | grep BlankRunner
```

---

## Ciclo de Desarrollo

```
1. Edit Player.kt (moveSpeed = 200f)
   ↓
2. buildinstall (2 min)
   ↓
3. Prueba en dispositivo
   ↓
4. ¿Está bien?
   → Sí: siguiente feature
   → No: volver a paso 1
```

---

## Cambios Comunes

### Aumentar Velocidad de Movimiento
```kotlin
// Player.kt line 14
private val moveSpeed = 150f  // Cambiar a 200f
```

### Saltos Más Altos
```kotlin
// Player.kt line 15
private val jumpPower = 400f  // Cambiar a 500f
```

### Más Plataformas
```kotlin
// GameScreen.kt setupPlatforms()
platforms.add(Platform(x = 350f, y = 200f, width = 100f))  // Nueva
```

### Cambiar Colores
```kotlin
// GameScreen.kt renderPlayer()
shapeRenderer.setColor(1f, 0f, 0f, 1f)  // Rojo en lugar de blanco
```

### Cambiar Tamaño del Jugador
```kotlin
// Player.kt constructor
val width: Float = 32f  // De 16f a 32f (más grande)
```

---

## Debug

### Ver Error en Compilación
```bash
gradle android:assembleDebug --stacktrace
```

### Ver Logs en Vivo
```bash
adb logcat -c  # Limpiar
gradle android:assembleDebug
buildinstall
adb logcat | grep -E "BlankRunner|ERROR|Exception"
```

### Verificar Dispositivo Conectado
```bash
adb devices
# Debe listar tu dispositivo
```

---

## Optimizaciones

### Build Más Rápido
```bash
# Agregar a ~/.gradle/gradle.properties:
org.gradle.parallel=true
org.gradle.workers.max=8
```

### Limpieza Completa (si buggeó)
```bash
gradle clean
buildinstall  # Toma ~3-5 min la primera vez limpia
```

### Ver Lo Que Cambió
```bash
git diff core/src/main/kotlin/com/blankrunner/Player.kt
```

---

## Commit y Push

Cuando termines un feature:
```bash
git add -A
git commit -m "feat: describe your changes"
git push origin claude/blank-runner-platformer-setup-ndyy7x
```

---

## Referencia Rápida

| Tarea | Comando | Tiempo |
|-------|---------|--------|
| Editar código | `vim core/src/.../Player.kt` | - |
| Compilar | `gradle android:assembleDebug` | 1-2 min |
| Instalar | `adb install -r android/build/outputs/apk/debug/android-debug.apk` | 10 sec |
| Ejecutar | `adb shell am start -n com.blankrunner/.AndroidLauncher` | 1 sec |
| Todo junto | `buildinstall` | 2 min |
| Ver logs | `adb logcat | grep BlankRunner` | - |
| Limpiar | `gradle clean` | 5 sec |

---

## Tips

1. **Usa alias** → Setup `buildinstall` en ~/.bashrc
2. **Edit + Test rápido** → El ciclo es 2 minutos
3. **Commit seguido** → No acumules cambios
4. **Prueba en dispositivo** → No solo en emulador

---

## Próximo Paso

Lee: `../QUICK_START.md` para setup completo.
