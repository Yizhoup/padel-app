package com.example.padel.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ForestDark,
    secondary = MossDark,
    tertiary = MossDark,
    background = Night,
    surface = NightSurface,
    surfaceVariant = Color(0xFF262E29),
    onPrimary = Night,
    onSecondary = Night,
    onBackground = White,
    onSurface = White,
    onSurfaceVariant = NightSlate,
)

private val LightColorScheme = lightColorScheme(
    primary = Forest,
    secondary = ForestSoft,
    tertiary = Moss,
    background = Sand,
    surface = White,
    surfaceVariant = Stone,
    onPrimary = White,
    onSecondary = White,
    onBackground = Ink,
    onSurface = Ink,
    onSurfaceVariant = Slate,
)

@Composable
fun PadelTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
