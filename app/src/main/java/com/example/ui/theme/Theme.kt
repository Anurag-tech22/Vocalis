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

private val DarkColorScheme = darkColorScheme(
    primary = VocalisEmerald,
    onPrimary = Color(0xFF003822),
    primaryContainer = Color(0xFF005234),
    onPrimaryContainer = Color(0xFF6FFFC4),
    secondary = VocalisAmber,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF78350F),
    onSecondaryContainer = Color(0xFFFEF3C7),
    tertiary = VocalisPurple,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF3B1861),
    onTertiaryContainer = Color(0xFFF3E8FF),
    background = VocalisObsidian,
    onBackground = VocalisTextPrimary,
    surface = VocalisSurface,
    onSurface = VocalisTextPrimary,
    surfaceVariant = VocalisSurfaceElevated,
    onSurfaceVariant = VocalisTextSecondary,
    outline = VocalisCardBorder
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF059669),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1FAE5),
    onPrimaryContainer = Color(0xFF064E3B),
    secondary = Color(0xFFD97706),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFEF3C7),
    onSecondaryContainer = Color(0xFF78350F),
    tertiary = Color(0xFF7C3AED),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFEDE9FE),
    onTertiaryContainer = Color(0xFF4C1D95),
    background = Color(0xFFF8FAF8),
    onBackground = Color(0xFF141815),
    surface = Color.White,
    onSurface = Color(0xFF141815),
    surfaceVariant = Color(0xFFEFF4F0),
    onSurfaceVariant = Color(0xFF4A554E),
    outline = Color(0xFFD1DCD3)
)

@Composable
fun VocalisTheme(
    darkTheme: Boolean = true, // Executive Carbon & Emerald dark theme by default
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Backwards compatibility alias
@Composable
fun PolarisTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) = VocalisTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
