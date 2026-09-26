package com.ayatkita.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColors = darkColorScheme(
    primary = SoftTeal,
    onPrimary = NightSlate,
    background = NightSlate,
    surface = NightSlate,
)

private val LightColors = lightColorScheme(
    primary = DeepTeal,
    onPrimary = WarmSand,
    background = WarmSand,
    surface = WarmSand,
    onBackground = TextDark,
    onSurface = TextDark
)

@Composable
fun AyatKitaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
