package com.shadowdash.game.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    secondary = NeonPurple,
    tertiary = GoldAccent,
    background = MidnightDark,
    surface = SurfaceDark,
    onPrimary = MidnightDark,
    onSecondary = MidnightDark,
    onBackground = TextLight,
    onSurface = TextLight
)

@Composable
fun ShadowDashTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}

