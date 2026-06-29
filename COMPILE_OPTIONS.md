# Blank Runner - Opciones de Compilación

## Resumen

| Opción | Requisitos | Dificultad | Tiempo | Resultado |
|--------|-----------|-----------|--------|-----------|
| **Android Studio** | AS + Android SDK | ⭐ Fácil | 5-10 min | APK + Debug |
| **Script Build** | JDK 11 + Android SDK | ⭐⭐ Medio | 3-5 min | APK Debug |
| **Docker** | Docker instalado | ⭐⭐ Medio | 10-15 min | APK Debug |
| **Gradle CLI** | JDK 11 + Android SDK | ⭐⭐⭐ Avanzado | 2-3 min | APK Debug |

---

## Opción 1: Android Studio (RECOMENDADO ⭐⭐⭐)

**Requisitos:**
- Android Studio 2023.1+
- Android SDK API 34
- Dispositivo/Emulador Android

**Pasos:**

### 1. Descargar e Instalar Android Studio
```bash
# macOS (con Homebrew)
brew install android-studio

# O descargar manualmente:
# https://developer.android.com/studio
```

### 2. Configurar Android SDK
1. Abrir Android Studio
2. `Preferences` (macOS) o `Settings` (Linux/Windows)
3. Navegar a: `Languages & Frameworks` → `Android SDK`
4. En la pestaña `SDK Platforms`:
   - ✓ Android 14 (API 34)
5. En la pestaña `SDK Tools`:
   - ✓ Android SDK Platform Tools
   - ✓ Android Emulator (opcional)
6. Click `Apply` y esperar a que descargue

### 3. Abrir el Proyecto
```bash
cd /path/to/blank-runner
open -a "Android Studio" .
# O en Linux:
# studio .
```

### 4. Esperar Gradle Sync
- Android Studio sincronizará automáticamente el proyecto
- Puede tomar 2-3 minutos la primera vez

### 5. Compilar y Ejecutar
- **Compilar**: `Build` → `Make Project` (Ctrl+F9)
- **Instalar**: `Run` → `Run 'android'` (Shift+F10)
- Seleccionar dispositivo/emulador si lo pide

**Ventajas:**
- ✓ GUI intuitiva
- ✓ Debugging integrado
- ✓ Emulador integrado
- ✓ Manejo automático de dependencias

**Desventajas:**
- ✗ Requiere descargar ~2GB (Android SDK)
- ✗ Requiere máquina con 8GB+ RAM

---

## Opción 2: Script Bash (Automatizado)

**Requisitos:**
- JDK 11+
- Gradle 8.1+
- Android SDK 34 previamente instalado

**Pasos:**

### 1. Hacer el Script Ejecutable
```bash
chmod +x BUILD_APK.sh
```

### 2. Ejecutar Build
```bash
./BUILD_APK.sh
```

El script:
- ✓ Verifica Java, Gradle, Android SDK
- ✓ Compila el APK automáticamente
- ✓ Proporciona el path del APK resultante

**Output Esperado:**
```
✓ Java: openjdk version "11.0.20"
✓ Gradle 8.14.3
✓ Android SDK: /home/usuario/Android/Sdk
✓ SDK Platform 34 encontrado

=== Compilando APK ===
> Task :android:assemble...
✓ BUILD EXITOSO
```

**Ventajas:**
- ✓ Automatizado
- ✓ Verifica requisitos
- ✓ Rápido

**Desventajas:**
- ✗ Requiere Android SDK instalado manualmente primero

---

## Opción 3: Docker (Container Aislado)

**Requisitos:**
- Docker instalado
- ~5GB espacio en disco

**Pasos:**

### 1. Construir Imagen Docker
```bash
docker build -f Dockerfile.android -t blank-runner-android .
```

Esto:
- Descarga Ubuntu 22.04
- Instala JDK 11
- Descarga e instala Android SDK + Build Tools
- Copia el proyecto
- Compila el APK

**Tiempo:** 10-15 minutos la primera vez (descarga ~2GB)

### 2. Compilar APK
```bash
docker run --rm -v $(pwd)/android/build/outputs:/outputs blank-runner-android
```

El APK estará en: `android/build/outputs/apk/debug/android-debug.apk`

**Ventajas:**
- ✓ Aislado del sistema
- ✓ Reproducible
- ✓ No requiere Android Studio
- ✓ Sin conflictos de dependencias

**Desventajas:**
- ✗ Requiere Docker instalado
- ✗ Más lento (primera vez ~15 min)
- ✗ ~5GB para la imagen

---

## Opción 4: Gradle CLI (Línea de Comandos)

**Requisitos:**
- JDK 11+
- Gradle 8.1+
- Android SDK 34 + Build Tools

**Pasos:**

### 1. Establecer ANDROID_HOME
```bash
# En ~/.bashrc o ~/.zshrc, agregar:
export ANDROID_HOME=/path/to/android/sdk
export PATH=$PATH:$ANDROID_HOME/platform-tools

# Luego:
source ~/.bashrc
```

### 2. Verificar Setup
```bash
echo $ANDROID_HOME
which sdkmanager
```

### 3. Compilar APK
```bash
cd /path/to/blank-runner
gradle android:assembleDebug
```

**Output:**
```
> Task :android:compileDebugKotlin
> Task :android:compileDebugResources
> Task :android:packageDebug
> Task :android:assembleDebug

BUILD SUCCESSFUL in 45s
```

El APK estará en: `android/build/outputs/apk/debug/android-debug.apk`

**Ventajas:**
- ✓ Muy rápido (~1-2 minutos)
- ✓ Control total
- ✓ Sin GUI

**Desventajas:**
- ✗ Requiere más configuración manual
- ✗ Sin debugging visual

---

## Instalar en Dispositivo

Una vez que tengas el APK, puedes instalarlo:

### Con Android Studio
```
Run → Run 'android' (Shift+F10)
```

### Con ADB (Command Line)
```bash
# Conectar dispositivo
adb connect 192.168.1.100:5555

# Instalar APK
adb install -r android/build/outputs/apk/debug/android-debug.apk

# Ejecutar
adb shell am start -n com.blankrunner/.AndroidLauncher
```

### Con archivo APK
1. Transferir `android/build/outputs/apk/debug/android-debug.apk` a tu dispositivo
2. Abrir File Manager en el dispositivo
3. Navegar al archivo APK
4. Tocar para instalar

---

## Troubleshooting

### "ANDROID_HOME not set"
```bash
export ANDROID_HOME=/path/to/android/sdk
# Luego verifica:
echo $ANDROID_HOME
```

### "SDK Platform 34 not found"
```bash
# Con Android Studio abierto:
# Settings → SDK Manager → SDK Platforms
# Marcar "Android 14 (API 34)"
# Click "Apply"

# O con CLI:
sdkmanager "platforms;android-34"
```

### "Gradle sync failed"
```bash
# Limpiar cache
gradle clean
gradle build
```

### "APK not found after build"
```bash
# Verificar que la compilación fue exitosa
gradle android:assembleDebug --stacktrace

# El APK debe estar en:
ls -lh android/build/outputs/apk/debug/
```

---

## Estimaciones de Tiempo

| Tarea | Tiempo |
|-------|--------|
| Instalar Android Studio | 10-15 min |
| Descargar Android SDK | 20-30 min |
| Primera compilación | 5-10 min |
| Compilaciones subsecuentes | 1-3 min |

---

## Recomendación Final

**Para desarrollo rápido:**
→ **Android Studio** (mejor experiencia visual + debugging)

**Para CI/CD o servidor:**
→ **Docker** (reproducible, aislado)

**Para desarrollo avanzado:**
→ **Gradle CLI** (máximo control, más rápido)

---

## Soporte MCP (Este Ambiente)

❌ **No disponible** compilar APK en este ambiente remoto porque:
- Falta Android SDK (requiere 2GB+)
- Restricciones de red para descargas
- Ambiente sin interfaz gráfica

✅ **Disponible** para:
- Proporcionar código fuente ✓
- Documentación de build ✓
- Scripts de automatización ✓
- Configuración de proyecto ✓
