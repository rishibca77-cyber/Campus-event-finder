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
    primary = CampusPrimaryDark,
    onPrimary = CampusOnPrimaryDark,
    primaryContainer = Color(0xFF1E3A8A),
    onPrimaryContainer = Color(0xFFDBEAFE),
    secondary = CampusSecondaryDark,
    onSecondary = Color(0xFF2E1065),
    secondaryContainer = Color(0xFF4C1D95),
    onSecondaryContainer = Color(0xFFEDE9FE),
    tertiary = CampusTertiaryDark,
    background = CampusBackgroundDark,
    onBackground = CampusOnBackgroundDark,
    surface = CampusSurfaceDark,
    onSurface = CampusOnSurfaceDark,
    surfaceVariant = CampusSurfaceVariantDark,
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = CampusOutlineDark,
    error = CampusError
)

private val LightColorScheme = lightColorScheme(
    primary = CampusPrimary,
    onPrimary = CampusOnPrimaryLight,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = Color(0xFF1E40AF),
    secondary = CampusSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEDE9FE),
    onSecondaryContainer = Color(0xFF5B21B6),
    tertiary = CampusTertiary,
    background = CampusBackgroundLight,
    onBackground = CampusOnBackgroundLight,
    surface = CampusSurfaceLight,
    onSurface = CampusOnSurfaceLight,
    surfaceVariant = CampusSurfaceVariantLight,
    onSurfaceVariant = Color(0xFF475569),
    outline = CampusOutlineLight,
    error = CampusError
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent campus tech theme
    content: @Composable () -> Unit,
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
