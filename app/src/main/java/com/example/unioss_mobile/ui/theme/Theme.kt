package com.example.unioss_mobile.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val UniossColorScheme = darkColorScheme(
    primary = AccentCyan,
    secondary = AccentGreen,
    background = BackgroundDark,
    surface = SurfaceDark,
    onPrimary = BackgroundDark,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = AccentRed
)

@Composable
fun UniossTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = UniossColorScheme,
        typography = Typography,
        content = content
    )
}