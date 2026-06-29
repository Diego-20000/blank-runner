# Blank Runner - Android Build Guide

## Project Structure

```
blank-runner/
├── core/                    # Game logic (shared code)
│   └── src/main/kotlin/com/blankrunner/
│       ├── BlankRunnerGame.kt
│       ├── Player.kt
│       ├── Platform.kt
│       ├── PhysicsEngine.kt
│       ├── GameScreen.kt
│       └── InputHandler.kt
│
├── android/                 # Android-specific code
│   ├── src/main/
│   │   ├── kotlin/com/blankrunner/
│   │   │   └── AndroidLauncher.kt
│   │   ├── res/
│   │   │   ├── drawable/    (app icon)
│   │   │   ├── values/      (strings.xml)
│   │   │   └── layout/      (layouts)
│   │   └── AndroidManifest.xml
│   ├── proguard-rules.pro
│   └── build.gradle
│
├── core/build.gradle        # Core module config
├── build.gradle             # Root config
├── settings.gradle          # Include modules
└── gradle.properties
```

## Prerequisites

### Required
- **Android Studio** (latest version)
- **Android SDK** (API 34)
- **JDK 11+** (Java Development Kit)
- **Gradle 8.1+**

### Android SDK Versions
- **Compile SDK**: 34 (Android 14)
- **Min SDK**: 21 (Android 5.0 Lollipop)
- **Target SDK**: 34 (Android 14)

## Building for Android

### 1. In Android Studio

```bash
# From root directory
gradle tasks

# Build debug APK
gradle assembleDebug

# Build release APK
gradle assembleRelease

# Install on connected device
gradle installDebug
```

### 2. Build Variants

**Debug Build** (for development/testing):
```bash
./gradlew android:assembleDebug
# Output: android/build/outputs/apk/debug/android-debug.apk
```

**Release Build** (for deployment):
```bash
./gradlew android:assembleRelease
# Output: android/build/outputs/apk/release/android-release.apk
```

## Running on Device/Emulator

### Prerequisites
- Android emulator running OR physical Android device connected
- Device with USB debugging enabled (physical device)

### Via Android Studio
1. Open Android Studio
2. Open `Project` → select root `blank-runner`
3. Wait for Gradle sync
4. Select `android` build variant (bottom)
5. Click `Run` (Shift+F10)

### Via Command Line

```bash
# List connected devices
adb devices

# Install debug APK
adb install android/build/outputs/apk/debug/android-debug.apk

# Run the app
adb shell am start -n com.blankrunner/.AndroidLauncher
```

## Testing the Game

### Touch Controls
- **Swipe Left**: Move player left
- **Swipe Right**: Move player right
- **Tap**: Jump

(Note: Current implementation uses keyboard input. For touch, modify `InputHandler.kt` to listen to touch events)

### Performance Testing
1. Build APK: `gradle assembleDebug`
2. Install: `adb install android/build/outputs/apk/debug/android-debug.apk`
3. Monitor in Android Studio Profiler:
   - CPU usage
   - Memory usage
   - Frame rate (60 FPS target)

## Project Configuration

### AndroidManifest.xml
- **Screen Orientation**: Landscape
- **Theme**: AppCompat Light (No ActionBar)
- **Immersive Mode**: Enabled (full screen)

### Permissions
- `INTERNET`: Allowed (future online features)

### Supported ABIs (CPU Architectures)
- armeabi-v7a (32-bit ARM)
- arm64-v8a (64-bit ARM)
- x86 (Intel 32-bit)
- x86_64 (Intel 64-bit)

## Development Workflow

### Adding Features
1. Add logic to `core/` module (game logic)
2. Update `InputHandler.kt` if input changes needed
3. Build: `gradle android:assembleDebug`
4. Test on device/emulator

### Debugging
```bash
# View logcat
adb logcat

# Filter by app
adb logcat | grep BlankRunner

# Clear logcat
adb logcat -c
```

## APK Installation Methods

### Method 1: Android Studio
1. Run → Run 'android' (Shift+F10)

### Method 2: Command Line
```bash
gradle installDebug
```

### Method 3: Manual (APK file)
1. Build APK: `gradle android:assembleDebug`
2. Transfer to device
3. Open file manager on device
4. Tap APK file to install

### Method 4: ADB
```bash
adb install -r build/outputs/apk/debug/android-debug.apk
```

## Troubleshooting

### "SDK not found"
```bash
# Download SDKs via Android Studio:
# SDK Manager → API Levels → Select 34
```

### "Gradle sync failed"
```bash
# Clean and rebuild
gradle clean
gradle build
```

### "App crashes on startup"
1. Check logcat for errors:
   ```bash
   adb logcat | grep AndroidLauncher
   ```
2. Ensure `core` module builds:
   ```bash
   gradle core:build
   ```

### Low FPS Performance
1. Profile in Android Studio Profiler
2. Check if rendering is GPU-bottlenecked
3. Reduce draw calls or optimize collision detection

## Build Configuration Reference

### Target Configuration
| Setting | Value |
|---------|-------|
| Compile SDK | 34 |
| Target SDK | 34 |
| Min SDK | 21 |
| JDK Version | 11 |
| Kotlin Version | 1.9.21 |
| LibGDX | 1.12.1 |

### APK Details
| Setting | Value |
|---------|-------|
| Application ID | com.blankrunner |
| Version Code | 1 |
| Version Name | 1.0 |
| Min Size | ~50 MB (debug) |
| Release Size | ~20-30 MB (depends on obfuscation) |

## Next Steps

1. **Setup Android Studio**:
   - Download and install latest Android Studio
   - Install Android SDK 34

2. **Open Project**:
   - File → Open → Select `blank-runner` folder
   - Wait for Gradle sync

3. **Run on Device**:
   - Connect Android device or start emulator
   - Run → Run 'android' (or press Shift+F10)

4. **Test Gameplay**:
   - Verify player movement
   - Check jump mechanics
   - Test platform collisions

## Resources

- [LibGDX Android Guide](https://libgdx.com/wiki/platforms/android)
- [Android Studio Docs](https://developer.android.com/studio/intro)
- [Gradle Build System](https://developer.android.com/build/gradle-intro)
