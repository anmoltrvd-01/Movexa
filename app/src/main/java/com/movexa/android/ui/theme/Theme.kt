package com.movexa.android.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = MovexaBlue,
    onPrimary = NeutralSurface,
    primaryContainer = MovexaBlueSurface,
    onPrimaryContainer = MovexaBlueDark,
    secondary = MovexaOrange,
    onSecondary = NeutralSurface,
    secondaryContainer = MovexaOrangeSurface,
    onSecondaryContainer = MovexaOrange,
    tertiary = MovexaGreen,
    onTertiary = NeutralSurface,
    tertiaryContainer = MovexaGreenSurface,
    onTertiaryContainer = MovexaGreen,
    background = NeutralBackground,
    onBackground = NeutralTextPrimary,
    surface = NeutralSurface,
    onSurface = NeutralTextPrimary,
    surfaceVariant = NeutralSurfaceVariant,
    onSurfaceVariant = NeutralTextSecondary,
    outline = NeutralBorder,
    outlineVariant = NeutralBorder.copy(alpha = 0.5f)
)

private val DarkColorScheme = darkColorScheme(
    primary = MovexaBlueDarkTheme,
    onPrimary = NeutralBackgroundDark,
    primaryContainer = MovexaBlueDark,
    onPrimaryContainer = MovexaBlueDarkTheme,
    secondary = MovexaOrangeDarkTheme,
    onSecondary = NeutralBackgroundDark,
    secondaryContainer = Color(0xFF3D2000),
    onSecondaryContainer = MovexaOrangeDarkTheme,
    background = NeutralBackgroundDark,
    onBackground = Color(0xFFE8EDF5),
    surface = NeutralSurfaceDark,
    onSurface = Color(0xFFE8EDF5),
    surfaceVariant = NeutralSurfaceVariantDark,
    onSurfaceVariant = NeutralTextHint,
    outline = Color(0xFF2D3748)
)

@Composable
fun MovexaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MovexaTypography,
        shapes = MovexaShapes,
        content = content
    )
}
