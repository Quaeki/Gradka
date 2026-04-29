package com.example.gradka.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val M3Light = lightColorScheme(
    primary = AccentLight,
    onPrimary = AccentInk,
    background = BgLight,
    surface = SurfaceLight,
    onBackground = InkLight,
    onSurface = InkLight,
)

private val M3Dark = darkColorScheme(
    primary = AccentDark,
    onPrimary = AccentInk,
    background = BgDark,
    surface = SurfaceDark,
    onBackground = InkDark,
    onSurface = InkDark,
)

@Composable
fun GradkaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val appColors = if (darkTheme) DarkAppColors else LightAppColors
    CompositionLocalProvider(LocalAppColors provides appColors) {
        MaterialTheme(
            colorScheme = if (darkTheme) M3Dark else M3Light,
            content = content,
        )
    }
}