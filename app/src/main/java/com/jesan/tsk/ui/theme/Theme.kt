package com.jesan.tsk.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Indigo,
    onPrimary = Color.White,
    primaryContainer = IndigoDark,
    onPrimaryContainer = Color.White,
    
    secondary = Purple,
    onSecondary = Color.White,
    secondaryContainer = Purple,
    onSecondaryContainer = Color.White,
    
    tertiary = PresentationGreen,
    onTertiary = Color.White,
    
    background = BackgroundDark,
    onBackground = TextPrimary,
    
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextSecondary,
    
    error = Error,
    onError = Color.White,
    
    outline = Color(0xFF64748B),
    outlineVariant = Color(0xFF334155)
)

private val LightColorScheme = lightColorScheme(
    primary = Indigo,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0E7FF),
    onPrimaryContainer = IndigoDark,
    
    secondary = Purple,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF3E8FF),
    onSecondaryContainer = Color(0xFF5B21B6),
    
    tertiary = PresentationGreen,
    onTertiary = Color.White,
    
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),
    
    error = Error,
    onError = Color.White,
    
    outline = Color(0xFF94A3B8),
    outlineVariant = Color(0xFFCBD5E1)
)

@Composable
fun AcademicxTSKTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
