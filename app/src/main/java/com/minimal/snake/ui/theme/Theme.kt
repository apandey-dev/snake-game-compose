package com.minimal.snake.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.minimal.snake.data.SnakeSkin
import com.minimal.snake.data.SnakeSkins
import com.minimal.snake.data.ThemeMode

@Immutable
data class ExtendedSnakeColors(
    val gridDot: Color,
    val surfaceCard: Color,
    val surfaceCardBorder: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val activeSkin: SnakeSkin,
    val isAmoled: Boolean
)

val LocalSnakeColors = staticCompositionLocalOf {
    ExtendedSnakeColors(
        gridDot = AmoledGridDot,
        surfaceCard = AmoledSurfaceVariant,
        surfaceCardBorder = AmoledBorder,
        textPrimary = AmoledTextPrimary,
        textSecondary = AmoledTextSecondary,
        activeSkin = SnakeSkins.ClassicBlue,
        isAmoled = true
    )
}

private val AmoledDarkColorScheme = darkColorScheme(
    primary = AccentBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1E3A8A),
    onPrimaryContainer = Color.White,
    background = AmoledBlack,
    onBackground = AmoledTextPrimary,
    surface = AmoledSurface,
    onSurface = AmoledTextPrimary,
    surfaceVariant = AmoledSurfaceVariant,
    onSurfaceVariant = AmoledTextSecondary,
    outline = AmoledBorder
)

private val MinimalLightColorScheme = lightColorScheme(
    primary = AccentBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = Color(0xFF1E3A8A),
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder
)

@Composable
fun SnakeGameTheme(
    themeMode: ThemeMode = ThemeMode.AMOLED_DARK,
    skin: SnakeSkin = SnakeSkins.ClassicBlue,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        ThemeMode.AMOLED_DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = if (isDark) {
        AmoledDarkColorScheme.copy(primary = skin.primaryColor)
    } else {
        MinimalLightColorScheme.copy(primary = skin.primaryColor)
    }

    val extendedColors = ExtendedSnakeColors(
        gridDot = if (isDark) AmoledGridDot else LightGridDot,
        surfaceCard = if (isDark) AmoledSurfaceVariant else LightSurfaceVariant,
        surfaceCardBorder = if (isDark) AmoledBorder else LightBorder,
        textPrimary = if (isDark) AmoledTextPrimary else LightTextPrimary,
        textSecondary = if (isDark) AmoledTextSecondary else LightTextSecondary,
        activeSkin = skin,
        isAmoled = isDark
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !isDark
            insetsController.isAppearanceLightNavigationBars = !isDark
        }
    }

    CompositionLocalProvider(LocalSnakeColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
