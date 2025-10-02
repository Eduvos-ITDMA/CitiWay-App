package com.example.citiway.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val defaultFontFamily = FontFamily.Default
private const val defaultLineHeight = 1.35f

val Typography = Typography(

    // 1. DISPLAY (Largest, for short, important text)
    displayLarge = TextStyle(
        fontFamily = defaultFontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 57.sp,
        lineHeight = (57.sp.value * defaultLineHeight).sp
    ),
    displayMedium = TextStyle(
        fontFamily = defaultFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp,
        lineHeight = (45.sp.value * defaultLineHeight).sp
    ),
    displaySmall = TextStyle(
        fontFamily = defaultFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 36.sp,
        lineHeight = (36.sp.value * defaultLineHeight).sp
    ),

    // 2. HEADLINE (Screen titles, prominent text)
    headlineLarge = TextStyle(
        fontFamily = defaultFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = (32.sp.value * defaultLineHeight).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = defaultFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = (28.sp.value * defaultLineHeight).sp
    ),
    headlineSmall = TextStyle(
        fontFamily = defaultFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = (24.sp.value * defaultLineHeight).sp
    ),

    // 3. TITLE (Subtitles, small headings, section headers)
    titleLarge = TextStyle(
        fontFamily = defaultFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = (22.sp.value * defaultLineHeight).sp
    ),
    titleMedium = TextStyle(
        fontFamily = defaultFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = (16.sp.value * defaultLineHeight).sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = defaultFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = (14.sp.value * defaultLineHeight).sp,
        letterSpacing = 0.1.sp
    ),

    // 4. BODY (Standard text, paragraphs)
    bodyLarge = TextStyle(
        fontFamily = defaultFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp, // The default text size for readability
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = defaultFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = defaultFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),

    // 5. LABEL (Buttons, captions, tiny text)
    labelLarge = TextStyle(
        fontFamily = defaultFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = defaultFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = defaultFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)
