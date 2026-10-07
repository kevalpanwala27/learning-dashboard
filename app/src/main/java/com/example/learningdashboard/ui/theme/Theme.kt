package com.example.learningdashboard.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
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

// Brand palette: lime is the signature accent, blue for secondary actions, orange for progress badges.
private val Lime = Color(0xFFD5EE6E)
private val LimeInk = Color(0xFF111500)
private val OliveForLight = Color(0xFF5C7A00)
private val Blue = Color(0xFF0A84FF)
private val Orange = Color(0xFFFFA64D)
private val OrangeInk = Color(0xFF2A1600)

private val DarkColors = darkColorScheme(
    primary = Lime,
    onPrimary = LimeInk,
    primaryContainer = Lime,
    onPrimaryContainer = LimeInk,
    secondary = Blue,
    onSecondary = Color.White,
    tertiary = Orange,
    onTertiary = OrangeInk,
    tertiaryContainer = Color(0xFF3A2A12),
    onTertiaryContainer = Color(0xFFFFC98A),
    background = Color(0xFF0C0C0C),
    onBackground = Color(0xFFF2F2F5),
    surface = Color(0xFF1A1A1D),
    onSurface = Color(0xFFF2F2F5),
    surfaceVariant = Color(0xFF27272B),
    onSurfaceVariant = Color(0xFF9C9CA6),
    outline = Color(0xFF3C3C43),
    outlineVariant = Color(0xFF2A2A2F),
    inverseSurface = Color(0xFFF2F2F5),
    inverseOnSurface = Color(0xFF0C0C0C),
    error = Color(0xFFFF6B6B),
    errorContainer = Color(0xFF3A1A1A),
    onErrorContainer = Color(0xFFFFB4B4),
)

private val LightColors = lightColorScheme(
    primary = OliveForLight,
    onPrimary = Color.White,
    primaryContainer = Lime,
    onPrimaryContainer = LimeInk,
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

/** Success green for completed lessons; not part of the Material scheme so kept here. */
@Composable
fun successColor(): Color = if (isSystemInDarkTheme()) Color(0xFF4ADE80) else Color(0xFF15803D)

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
