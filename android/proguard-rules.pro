# LibGDX ProGuard rules
-keep class com.badlogic.gdx.** { *; }
-keep interface com.badlogic.gdx.** { *; }

# Kotlin
-keep class kotlin.** { *; }
-keepclassmembers class kotlin.** {*;}

# Blank Runner
-keep class com.blankrunner.** { *; }
-keepclassmembers class com.blankrunner.** { *; }

# Android
-keep public class android.** { *; }
-keep interface android.** { *; }
