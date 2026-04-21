package com.confessio.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val ConfessioColorScheme = darkColorScheme(
    primary = ConfessioGold,
    onPrimary = ConfessioBg,
    background = ConfessioBg,
    surface = ConfessioSurface,
    onBackground = ConfessioInk,
    onSurface = ConfessioInk,
    error = ConfessioError
)

@Composable
fun ConfessioTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ConfessioColorScheme,
        typography = Typography,
        content = content
    )
}
