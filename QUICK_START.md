# Blank Runner - Inicio Rápido (Gradle CLI)

## Setup Inicial (Una sola vez)

### macOS
```bash
# 1. Instalar Homebrew (si no lo tienes)
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# 2. Instalar JDK 11
brew install openjdk@11
sudo ln -sfn /usr/local/opt/openjdk@11/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-11.jdk

# 3. Descargar Android Studio (GUI para instalar SDK fácil)
brew install android-studio

# 4. Abrir Android Studio y instalar SDK 34
open /Applications/Android\ Studio.app
# → Settings → Languages & Frameworks → Android SDK
# → SDK Platforms → Marcar "Android 14 (API 34)"
# → Apply

# 5. Configurar ANDROID_HOME
echo 'export ANDROID_HOME=$HOME/Library/Android/sdk' >> ~/.zshrc
echo 'export PATH=$PATH:$ANDROID_HOME/platform-tools' >> ~/.zshrc
source ~/.zshrc
```

### Linux (Ubuntu/Debian)
```bash
# 1. Instalar JDK 11
sudo apt-get update
sudo apt-get install -y openjdk-11-jdk-headless

# 2. Descargar Android Studio
# https://developer.android.com/studio → Descargar manualmente

# 3. Extraer y ejecutar
unzip android-studio-*.zip
cd android-studio/bin
./studio.sh

# 4. Instalar SDK 34 desde Android Studio UI
# → Settings → Languages & Frameworks → Android SDK
# → SDK Platforms → Marcar "Android 14 (API 34)"
# → Apply

# 5. Configurar ANDROID_HOME (típicamente aquí)
echo 'export ANDROID_HOME=$HOME/Android/Sdk' >> ~/.bashrc
echo 'export PATH=$PATH:$ANDROID_HOME/platform-tools' >> ~/.bashrc
source ~/.bashrc
```

### Windows (PowerShell)
```powershell
# 1. Instalar Chocolatey (si no lo tienes)
Set-ExecutionPolicy Bypass -Scope Process -Force
[System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072
iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

# 2. Instalar JDK 11
choco install openjdk11 -y

# 3. Descargar Android Studio
choco install android-studio -y

# 4. Ejecutar Android Studio e instalar SDK 34
# Settings → Languages & Frameworks → Android SDK
# → SDK Platforms → Marcar "Android 14 (API 34)"
# → Apply

# 5. Configurar ANDROID_HOME (en Variables de Entorno del Sistema)
# Nueva variable: ANDROID_HOME = C:\Users\TuUsuario\AppData\Local\Android\Sdk
# Editar PATH y agregar: %ANDROID_HOME%\platform-tools

# 6. Verificar en nueva PowerShell
echo $env:ANDROID_HOME
```

### Verificar Setup
```bash
# Todos los sistemas:
java -version          # Debe ser openjdk 11+
gradle -v              # Debe ser 8.1+
echo $ANDROID_HOME     # Debe mostrar ruta
ls $ANDROID_HOME/platforms/android-34  # Debe existir
```

---

## Compilación Rápida (Cada vez que quieras buildear)

### Build Debug APK (Lo más rápido: ~1-2 min)
```bash
cd /path/to/blank-runner
gradle android:assembleDebug
```

Output: `android/build/outputs/apk/debug/android-debug.apk`

### Alias Rápido (Opcional pero muy útil)
```bash
# Agregar a ~/.bashrc o ~/.zshrc:
alias buildapk='cd ~/blank-runner && gradle android:assembleDebug && echo "✓ APK listo en android/build/outputs/apk/debug/android-debug.apk"'

# Luego usar así:
buildapk
```

### Build Release APK (Para publicar)
```bash
gradle android:assembleRelease
# Output: android/build/outputs/apk/release/android-release-unsigned.apk
```

---

## Instalar en Dispositivo (Después de compilar)

### Con ADB (Más rápido)
```bash
# Conectar dispositivo vía USB o WiFi
adb connect 192.168.1.100:5555

# Instalar APK
adb install -r android/build/outputs/apk/debug/android-debug.apk

# Ejecutar
adb shell am start -n com.blankrunner/.AndroidLauncher

# Ver logs
adb logcat | grep BlankRunner
```

### Alias para Install+Run
```bash
# Agregar a ~/.bashrc o ~/.zshrc:
alias installapk='adb install -r android/build/outputs/apk/debug/android-debug.apk && adb shell am start -n com.blankrunner/.AndroidLauncher && echo "✓ App ejecutándose"'

# Usar:
installapk
```

---

## Flujo de Desarrollo Completo (Super Rápido)

### Setup Alias (Primera vez)
```bash
cat >> ~/.bashrc << 'EOF'
# Blank Runner Development
alias buildapk='cd ~/blank-runner && gradle android:assembleDebug'
alias installapk='adb install -r ~/blank-runner/android/build/outputs/apk/debug/android-debug.apk && adb shell am start -n com.blankrunner/.AndroidLauncher'
alias buildinstall='buildapk && installapk'
EOF
source ~/.bashrc
```

### Iteración de Desarrollo
```bash
# Editar código en tu editor favorito
# vim/nano/VSCode/etc.

# Build + Install en UN comando (~2 min):
buildinstall

# Ver cambios en dispositivo inmediatamente
# ✓ Listo para probar
```

---

## Optimizaciones

### Caché de Gradle (Ya optimizado)
```bash
# Los builds subsecuentes son más rápidos (~1 min)
# porque Gradle cachea dependencias

# Limpiar caché si tienes problemas:
gradle clean
```

### Compilación Incremental
```bash
# Gradle compila solo lo que cambió
# Primera compilación: ~2 min
# Siguientes: ~30-60 segundos
```

### Monitoreo en Tiempo Real (Watch Mode)
```bash
# Ver cambios en vivo mientras editas:
gradle android:assembleDebug --continuous

# Ctrl+C para detener
```

### Parallelizar Tareas (Si tienes CPU multi-core)
```bash
# Agregar a ~/.gradle/gradle.properties:
org.gradle.parallel=true
org.gradle.workers.max=4  # Ajusta al número de cores
```

---

## Tiempo Esperado

| Tarea | Tiempo | Comando |
|-------|--------|---------|
| Setup inicial | 30-60 min | Una sola vez |
| Primera compilación | 2-3 min | `gradle android:assembleDebug` |
| Compilaciones subsecuentes | 1-2 min | `gradle android:assembleDebug` |
| Limpiar + compilar | 5-10 min | `gradle clean android:assembleDebug` |
| Build + Install | 2-3 min | `buildinstall` |

---

## Comandos Útiles

```bash
# Ver tareas disponibles
gradle tasks

# Compilar solo core (sin Android)
gradle core:build

# Ver información de compilación
gradle android:assembleDebug --info

# Forzar compilación completa
gradle android:assembleDebug --rerun-tasks

# Verificar lint (problemas)
gradle lint

# Ver estructura del proyecto
gradle projects
```

---

## Troubleshooting Rápido

```bash
# "BUILD FAILED"
gradle clean
gradle android:assembleDebug

# "SDK not found"
echo $ANDROID_HOME
ls $ANDROID_HOME/platforms/android-34

# "Cannot find android dependency"
gradle --refresh-dependencies android:assembleDebug

# APK muy grande
gradle android:assembleRelease  # Release es más pequeño
```

---

## Próximos Pasos Recomendados

1. **Setup inicial** (~30 min) → Sigue instrucciones arriba
2. **Primera compilación** (~3 min) → `gradle android:assembleDebug`
3. **Crear alias** (~1 min) → Copia los comandos en ~/.bashrc
4. **Iteración** → Edit código + `buildinstall`

---

## Validación Post-Setup

```bash
# Ejecutar este script para verificar todo:
echo "=== Verificando Setup ===" && \
java -version && \
gradle -v && \
echo "ANDROID_HOME: $ANDROID_HOME" && \
ls -d $ANDROID_HOME/platforms/android-34 && \
echo "✓ ¡Setup completado! Ya puedes compilar." || \
echo "✗ Algo falta. Revisa los pasos arriba."
```

---

## Resumen: Flujo Más Rápido a Futuro

```
┌─────────────────────────────────────────┐
│ 1. Setup Inicial (30 min, una sola vez) │
│    - JDK 11, Android SDK 34            │
│    - Configurar ANDROID_HOME           │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│ 2. Desarrollo Iterativo (RÁPIDO)        │
│                                         │
│  $ vim src/...      ← Edit código       │
│  $ buildinstall     ← 2 min (Build+Run)│
│  ✓ Ver cambios      ← En dispositivo    │
│                                         │
│  Repetir...                             │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│ 3. Release (Cuando esté listo)          │
│    gradle android:assembleRelease       │
└─────────────────────────────────────────┘
```

**⏱️ Tiempo por iteración después del setup: ~2 minutos**
