package ru.filimonov.hpa.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import ru.filimonov.hpa.R

private val Raleway = FontFamily(
    Font(R.font.raleway_light, FontWeight.Light),
    Font(R.font.raleway_medium, FontWeight.Medium),
    Font(R.font.raleway_regular, FontWeight.Normal),
)

val HpaTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = Raleway,
        lineHeight = 64.sp,
        fontSize = 57.sp,
        letterSpacing = (-0.2).sp,
        fontWeight = FontWeight.Medium,
    ),
    displayMedium = TextStyle(
        fontFamily = Raleway,
        lineHeight = 52.sp,
        fontSize = 45.sp,
        letterSpacing = 0.sp,
        fontWeight = FontWeight.Medium,
    ),
    displaySmall = TextStyle(
        fontFamily = Raleway,
        lineHeight = 44.sp,
        fontSize = 36.sp,
        letterSpacing = 0.sp,
        fontWeight = FontWeight.Medium,
    ),
    headlineLarge = TextStyle(
        fontFamily = Raleway,
        lineHeight = 40.sp,
        fontSize = 32.sp,
        letterSpacing = 0.sp,
        fontWeight = FontWeight.Medium,
    ),
    headlineMedium = TextStyle(
        fontFamily = Raleway,
        lineHeight = 36.sp,
        fontSize = 28.sp,
        letterSpacing = 0.sp,
        fontWeight = FontWeight.Medium,
    ),
    headlineSmall = TextStyle(
        fontFamily = Raleway,
        lineHeight = 32.sp,
        fontSize = 24.sp,
        letterSpacing = 0.sp,
        fontWeight = FontWeight.Medium,
    ),
    titleLarge = TextStyle(
        fontFamily = Raleway,
        lineHeight = 28.sp,
        fontSize = 22.sp,
        letterSpacing = 0.sp,
        fontWeight = FontWeight.Normal,
    ),
    titleMedium = TextStyle(
        fontFamily = Raleway,
        lineHeight = 24.sp,
        fontSize = 16.sp,
        letterSpacing = 0.2.sp,
        fontWeight = FontWeight.Normal,
    ),
    titleSmall = TextStyle(
        fontFamily = Raleway,
        lineHeight = 20.sp,
        fontSize = 14.sp,
        letterSpacing = 0.1.sp,
        fontWeight = FontWeight.Normal,
    ),
    labelLarge = TextStyle(
        fontFamily = Raleway,
        lineHeight = 20.sp,
        fontSize = 14.sp,
        letterSpacing = 0.1.sp,
        fontWeight = FontWeight.Normal,
    ),
    labelMedium = TextStyle(
        fontFamily = Raleway,
        lineHeight = 16.sp,
        fontSize = 12.sp,
        letterSpacing = 0.5.sp,
        fontWeight = FontWeight.Normal,
    ),
    labelSmall = TextStyle(
        fontFamily = Raleway,
        lineHeight = 16.sp,
        fontSize = 11.sp,
        letterSpacing = 0.5.sp,
        fontWeight = FontWeight.Normal,
    ),
    bodyLarge = TextStyle(
        fontFamily = Raleway,
        lineHeight = 24.sp,
        fontSize = 16.sp,
        letterSpacing = 0.5.sp,
        fontWeight = FontWeight.Light,
    ),
    bodyMedium = TextStyle(
        fontFamily = Raleway,
        lineHeight = 20.sp,
        fontSize = 14.sp,
        letterSpacing = 0.2.sp,
        fontWeight = FontWeight.Light,
    ),
    bodySmall = TextStyle(
        fontFamily = Raleway,
        lineHeight = 16.sp,
        fontSize = 12.sp,
        letterSpacing = 0.4.sp,
        fontWeight = FontWeight.Light,
    )
)
