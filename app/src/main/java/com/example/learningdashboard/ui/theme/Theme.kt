package com.example.learningdashboard.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Brand palette. Indigo for primary actions, violet as the gradient partner, amber for the offline notice.
private val Indigo600 = Color(0xFF4F46E5)
private val Indigo300 = Color(0xFFA5B4FC)
private val Indigo100 = Color(0xFFE0E7FF)
private val Indigo950 = Color(0xFF1E1B4B)
private val Violet600 = Color(0xFF7C3AED)
private val Violet300 = Color(0xFFC4B5FD)

private val LightColors = lightColorScheme(
    primary = Indigo600,
    onPrimary = Color.White,
    primaryContainer = Indigo100,
    onPrimaryContainer = Indigo950,
    secondary = Violet600,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEDE9FE),
    onSecondaryContainer = Color(0xFF2E1065),
    tertiaryContainer = Color(0xFFFFF4CC),
    onTertiaryContainer = Color(0xFF6B4E00),
    background = Color(0xFFF4F4FB),
    onBackground = Color(0xFF17172B),
    surface = Color.White,
    onSurface = Color(0xFF17172B),
    surfaceVariant = Color(0xFFEDEDF7),
    onSurfaceVariant = Color(0xFF5B5B75),
    outline = Color(0xFFC6C6DC),
    outlineVariant = Color(0xFFE3E3F0),
    error = Color(0xFFB3261E),
    errorContainer = Color(0xFFFCE8E6),
    onErrorContainer = Color(0xFF5C120D),
)

private val DarkColors = darkColorScheme(
    primary = Indigo300,
    onPrimary = Indigo950,
    primaryContainer = Color(0xFF3730A3),
    onPrimaryContainer = Indigo100,
    secondary = Violet300,
    onSecondary = Color(0xFF2E1065),
    secondaryContainer = Color(0xFF4C1D95),
    onSecondaryContainer = Color(0xFFEDE9FE),
    tertiaryContainer = Color(0xFF4A3A00),
    onTertiaryContainer = Color(0xFFFFE08A),
    background = Color(0xFF0E0F1D),
    onBackground = Color(0xFFE6E6F2),
    surface = Color(0xFF171829),
    onSurface = Color(0xFFE6E6F2),
    surfaceVariant = Color(0xFF242539),
    onSurfaceVariant = Color(0xFFA9A9C4),
    outline = Color(0xFF4A4B66),
    outlineVariant = Color(0xFF2E2F45),
    error = Color(0xFFF2B8B5),
    errorContainer = Color(0xFF5C120D),
    onErrorContainer = Color(0xFFFCE8E6),
)

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

private val AppTypography = Typography(
    headlineLarge = TextStyle(fontSize = 32.sp, lineHeight = 38.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.5).sp),
    headlineMedium = TextStyle(fontSize = 26.sp, lineHeight = 32.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.3).sp),
    titleLarge = TextStyle(fontSize = 20.sp, lineHeight = 26.sp, fontWeight = FontWeight.SemiBold),
    titleMedium = TextStyle(fontSize = 17.sp, lineHeight = 22.sp, fontWeight = FontWeight.SemiBold),
    bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontSize = 12.sp, lineHeight = 16.sp),
    labelLarge = TextStyle(fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.1.sp),
    labelMedium = TextStyle(fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.SemiBold),
)

/** The one gradient used by every hero header and primary button. */
@Composable
fun heroGradient(): Brush {
    val dark = isSystemInDarkTheme()
    return Brush.linearGradient(
        colors = if (dark) listOf(Color(0xFF3730A3), Color(0xFF5B21B6)) else listOf(Indigo600, Violet600),
    )
}

@Composable
fun LearningDashboardTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        shapes = AppShapes,
        typography = AppTypography,
        content = content,
    )
}
