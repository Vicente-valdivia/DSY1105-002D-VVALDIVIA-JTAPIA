package com.example.dsy1105_002d_vvaldivia_jtapia.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = MinimalPrimaryBlue,
    secondary = MinimalPrimaryBlueVariant,
    background = MinimalBackground,
    surface = MinimalSurface,
    onPrimary = MinimalTextPrimary,
    onSecondary = MinimalTextPrimary,
    onBackground = MinimalTextPrimary,
    onSurface = MinimalTextPrimary
)

@Composable
fun DSY1105002DVVALDIVIAJTAPIATheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = MinimalBackground.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}