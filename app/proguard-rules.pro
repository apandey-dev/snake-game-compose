# ==============================================================================
# Security, Obfuscation & Size Optimization Rules for Minimal Snake Game
# ==============================================================================

# Enable maximum R8 optimization passes
-optimizationpasses 5
-allowaccessmodification
-repackageclasses ''
-overloadaggressively

# Strip all debug information and source file attributes to thwart reverse engineering
-renamesourcefileattribute ""
-keepattributes !SourceFile,!LineNumberTable,!LocalVariableTable,!LocalVariableTypeTable

# Remove all Android log statements from release build
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
    public static int e(...);
    public static int println(...);
}

# Keep Compose runtime essentials
-keep class androidx.compose.runtime.** { *; }
-keepclassmembers class * {
    @androidx.compose.runtime.Composable *;
    @androidx.compose.runtime.ReadOnlyComposable *;
}

# Keep DataStore Preferences models
-keepclassmembers class * extends androidx.datastore.preferences.core.Preferences { *; }

# Obfuscate internal models and engine logic safely
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
