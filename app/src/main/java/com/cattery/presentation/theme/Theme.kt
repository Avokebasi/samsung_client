package com.cattery.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val CatteryColorScheme = lightColorScheme(
    primary = BluePrimary,
    onPrimary = WhiteBackground,
    primaryContainer = BlueLight,
    onPrimaryContainer = BlueDark,
    secondary = BluePrimary,
    onSecondary = WhiteBackground,
    background = WhiteBackground,
    onBackground = TextPrimary,
    surface = CardBackground,
    onSurface = TextPrimary,
    surfaceVariant = BlueLight,
    onSurfaceVariant = TextSecondary,
    outline = BluePrimary.copy(alpha = 0.5f),
)

private val CatteryTypography = Typography(
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        color = TextPrimary,
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        color = TextPrimary,
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        color = TextPrimary,
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        color = TextPrimary,
    ),
    bodyLarge = TextStyle(
        fontSize = 16.sp,
        color = TextPrimary,
    ),
    bodyMedium = TextStyle(
        fontSize = 14.sp,
        color = TextSecondary,
    ),
)

@Composable
fun CatteryTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CatteryColorScheme,
        typography = CatteryTypography,
        content = content,
    )
}
