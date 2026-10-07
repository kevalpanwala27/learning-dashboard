package com.example.learningdashboard.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.learningdashboard.R

// Brand palette: one accent only. Olive carries every primary signal (CTA, progress, completion,
// loaders); orange marks percentage badges; blue is reserved for secondary actions.
private val Olive = Color(0xFF5C7A00)
private val OliveSoft = Color(0xFFE9F0D6)
private val Blue = Color(0xFF0A84FF)

/** Light only, by design: one theme means every screen is designed rather than auto-inverted. */
private val LightColors = lightColorScheme(
    primary = Olive,
    onPrimary = Color.White,
    primaryContainer = Olive,
    onPrimaryContainer = Color.White,
    surfaceTint = Olive,
    secondary = Blue,
    onSecondary = Color.White,
    tertiary = Color(0xFFE07B00),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFF0D6),
    onTertiaryContainer = Color(0xFF6B3F00),
    background = Color(0xFFF5F5F8),
    onBackground = Color(0xFF0C0C0C),
    surface = Color.White,
    onSurface = Color(0xFF0C0C0C),
    surfaceVariant = Color(0xFFEEEEF2),
    onSurfaceVariant = Color(0xFF6A6A75),
    outline = Color(0xFFD6D6DD),
    outlineVariant = Color(0xFFE8E8EE),
    inverseSurface = Color(0xFF0C0C0C),
    inverseOnSurface = Color.White,
    error = Color(0xFFD93025),
    errorContainer = Color(0xFFFDE7E5),
    onErrorContainer = Color(0xFF5C120D),
)

/** Soft olive wash used behind the login header. */
val AccentWash = OliveSoft

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

val Inter = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
    Font(R.font.inter_bold, FontWeight.Bold),
)

private val AppTypography = Typography(
    displaySmall = TextStyle(fontFamily = Inter, fontSize = 48.sp, lineHeight = 52.sp, fontWeight = FontWeight.Bold, letterSpacing = (-1).sp),
    headlineLarge = TextStyle(fontFamily = Inter, fontSize = 32.sp, lineHeight = 38.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.6).sp),
    headlineMedium = TextStyle(fontFamily = Inter, fontSize = 26.sp, lineHeight = 32.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.4).sp),
    titleLarge = TextStyle(fontFamily = Inter, fontSize = 20.sp, lineHeight = 26.sp, fontWeight = FontWeight.SemiBold, letterSpacing = (-0.2).sp),
    titleMedium = TextStyle(fontFamily = Inter, fontSize = 17.sp, lineHeight = 22.sp, fontWeight = FontWeight.SemiBold),
    bodyLarge = TextStyle(fontFamily = Inter, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = Inter, fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontFamily = Inter, fontSize = 12.sp, lineHeight = 16.sp),
    labelLarge = TextStyle(fontFamily = Inter, fontSize = 15.sp, lineHeight = 20.sp, fontWeight = FontWeight.SemiBold),
    labelMedium = TextStyle(fontFamily = Inter, fontSize = 13.sp, lineHeight = 16.sp, fontWeight = FontWeight.SemiBold),
    labelSmall = TextStyle(fontFamily = Inter, fontSize = 11.sp, lineHeight = 14.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.8.sp),
)

@Composable
fun LearningDashboardTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        shapes = AppShapes,
        typography = AppTypography,
        content = content,
    )
}
