package com.example.transcriptu.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = TextInverse,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = PrimaryDark,
    secondary = AccentPurple,
    onSecondary = TextInverse,
    secondaryContainer = AccentPurpleContainer,
    onSecondaryContainer = AccentPurple,
    background = Background,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = BorderDefault,
    outlineVariant = BorderStrong,
    error = Error,
    onError = TextInverse,
    errorContainer = ErrorContainer,
    onErrorContainer = Error,
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryLight,
    onPrimary = TextInverse,
    primaryContainer = PrimaryDark,
    onPrimaryContainer = PrimaryLight,
    secondary = Color(0xFFBB9FFF),
    onSecondary = Color(0xFF1A004F),
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorderDefault,
    outlineVariant = DarkBorderStrong,
    error = Color(0xFFFF6B6B),
    onError = Color(0xFF600000),
    errorContainer = Color(0xFF400000),
    onErrorContainer = Color(0xFFFFB3B3),
)

@Composable
fun TranscriptuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}