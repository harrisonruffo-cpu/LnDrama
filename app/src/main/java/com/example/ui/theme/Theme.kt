package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val CinematicDarkColorScheme = darkColorScheme(
    primary = CrimsonPrimary,
    onPrimary = Color.White,
    primaryContainer = CrimsonDark,
    onPrimaryContainer = Color.White,
    secondary = GoldAccent,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF3B3005),
    onSecondaryContainer = GoldAccent,
    tertiary = CrimsonLight,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = DarkSurfaceBorder
)

private val CinematicLightColorScheme = lightColorScheme(
    primary = CrimsonPrimary,
    onPrimary = Color.White,
    secondary = GoldAccent,
    onSecondary = Color.Black,
    background = Color(0xFFFBFBFC),
    onBackground = Color(0xFF151518),
    surface = Color.White,
    onSurface = Color(0xFF151518),
    surfaceVariant = Color(0xFFF0EFF4),
    onSurfaceVariant = Color(0xFF555562),
    outline = Color(0xFFDCDCE5)
)

@Composable
fun LitoralNovelasTheme(
    darkTheme: Boolean = true, // Default to sleek dark cinema experience
    dynamicColor: Boolean = false, // Keep the custom branding rich and cohesive
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) CinematicDarkColorScheme else CinematicLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Retained alias for backwards compatibility
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    LitoralNovelasTheme(darkTheme = darkTheme, content = content)
}

