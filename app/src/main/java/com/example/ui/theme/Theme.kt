package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = GoldAccent,
    onPrimary = Color.Black,
    primaryContainer = PurpleDeep,
    onPrimaryContainer = Color.White,
    secondary = TealNeon,
    onSecondary = Color.Black,
    tertiary = PinkNeon,
    background = DarkBackground,
    onBackground = Color.White,
    surface = PurpleCard,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF281C48),
    onSurfaceVariant = Color(0xFFE2D8F6)
)

private val LightColorScheme = darkColorScheme(
    primary = GoldAccent,
    onPrimary = Color.Black,
    primaryContainer = PurpleDeep,
    onPrimaryContainer = Color.White,
    secondary = TealNeon,
    onSecondary = Color.Black,
    tertiary = PinkNeon,
    background = DarkBackground,
    onBackground = Color.White,
    surface = PurpleCard,
    onSurface = Color.White
)

@Composable
fun SkySurferTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    SkySurferTheme(darkTheme = darkTheme, content = content)
}
