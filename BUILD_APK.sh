#!/bin/bash
set -e

echo "╔════════════════════════════════════════════════════════════════╗"
echo "║     Blank Runner - APK Build Script                           ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""

# Detectar sistema operativo
OS="$(uname)"
if [[ "$OS" == "Darwin" ]]; then
    echo "✓ Detectado: macOS"
elif [[ "$OS" == "Linux" ]]; then
    echo "✓ Detectado: Linux"
else
    echo "✗ Sistema no soportado: $OS"
    exit 1
fi

echo ""
echo "=== Requisitos ==="
echo ""

# Verificar Java
if ! command -v java &> /dev/null; then
    echo "✗ Java no encontrado"
    echo "  Instala JDK 11+ desde: https://adoptium.net/"
    exit 1
fi
JAVA_VERSION=$(java -version 2>&1 | head -1)
echo "✓ Java: $JAVA_VERSION"

# Verificar Gradle
if ! command -v gradle &> /dev/null; then
    echo "✗ Gradle no encontrado"
    echo "  Instala desde: https://gradle.org/install/"
    exit 1
fi
GRADLE_VERSION=$(gradle -v 2>&1 | grep Gradle)
echo "✓ $GRADLE_VERSION"

# Verificar Android SDK
if [[ -z "$ANDROID_HOME" ]]; then
    echo ""
    echo "✗ ANDROID_HOME no configurado"
    echo ""
    echo "Para instalar Android SDK:"
    echo ""
    if [[ "$OS" == "Darwin" ]]; then
        echo "  1. Descargar Android Studio:"
        echo "     https://developer.android.com/studio"
        echo ""
        echo "  2. Instalar y ejecutar Android Studio"
        echo ""
        echo "  3. Abrir Preferences → Appearance & Behavior → System Settings → Android SDK"
        echo ""
        echo "  4. Instalar SDK Platform 34 y NDK (si es necesario)"
        echo ""
        echo "  5. Copiar la ruta SDK (por ejemplo: /Users/usuario/Library/Android/sdk)"
        echo ""
        echo "  6. Configurar variable de entorno:"
        echo "     echo 'export ANDROID_HOME=/Users/usuario/Library/Android/sdk' >> ~/.zshrc"
        echo "     source ~/.zshrc"
    else
        echo "  1. Descargar Android Studio:"
        echo "     https://developer.android.com/studio"
        echo ""
        echo "  2. Instalar y ejecutar Android Studio"
        echo ""
        echo "  3. Abrir Settings → Languages & Frameworks → Android SDK"
        echo ""
        echo "  4. Instalar SDK Platform 34"
        echo ""
        echo "  5. Copiar la ruta SDK (por ejemplo: /home/usuario/Android/Sdk)"
        echo ""
        echo "  6. Configurar variable de entorno:"
        echo "     echo 'export ANDROID_HOME=/home/usuario/Android/Sdk' >> ~/.bashrc"
        echo "     source ~/.bashrc"
    fi
    exit 1
fi
echo "✓ Android SDK: $ANDROID_HOME"

# Verificar compileSdk
if [[ ! -d "$ANDROID_HOME/platforms/android-34" ]]; then
    echo ""
    echo "✗ Android SDK Platform 34 no encontrado en:"
    echo "  $ANDROID_HOME/platforms/"
    echo ""
    echo "Instala desde Android Studio:"
    echo "  - Tools → SDK Manager"
    echo "  - Selecciona API Level 34"
    echo "  - Click 'Apply'"
    exit 1
fi
echo "✓ SDK Platform 34 encontrado"

echo ""
echo "=== Compilando APK ==="
echo ""

# Compilar debug APK
echo "Ejecutando: gradle android:assembleDebug"
gradle android:assembleDebug

echo ""
echo "╔════════════════════════════════════════════════════════════════╗"
echo "║                  ✓ BUILD EXITOSO                             ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""

APK_PATH="android/build/outputs/apk/debug/android-debug.apk"
if [[ -f "$APK_PATH" ]]; then
    APK_SIZE=$(du -h "$APK_PATH" | cut -f1)
    echo "APK: $APK_PATH"
    echo "Tamaño: $APK_SIZE"
    echo ""
    echo "Para instalar en tu dispositivo:"
    echo "  adb connect <IP_DEL_DISPOSITIVO>:5555"
    echo "  adb install -r $APK_PATH"
    echo ""
    echo "O desde Android Studio:"
    echo "  Run → Run 'android'"
else
    echo "✗ APK no encontrado en: $APK_PATH"
    exit 1
fi
