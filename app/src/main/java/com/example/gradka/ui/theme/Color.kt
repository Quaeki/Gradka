package com.example.gradka.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Design tokens — light
val BgLight        = Color(0xFFFAFAF7)
val SurfaceLight   = Color(0xFFFFFFFF)
val Surface2Light  = Color(0xFFF2EFE8)
val Surface3Light  = Color(0xFFE8E4DB)
val InkLight       = Color(0xFF1A1A17)
val Ink2Light      = Color(0xFF4A4A44)
val Ink3Light      = Color(0xFF8A8A82)
val LineLight      = Color(0xFFE5E2D8)
val Line2Light     = Color(0xFFD6D2C6)
val AccentLight    = Color(0xFF5A8848)   // oklch(0.63 0.12 125) ≈
val AccentSoft     = Color(0xFFE6F3DF)   // oklch(0.94 0.04 125) ≈
val AccentDeep     = Color(0xFF2C5222)   // oklch(0.42 0.08 125) ≈
val AccentInk      = Color(0xFFFFFFFF)
val Danger         = Color(0xFFCF5028)   // oklch(0.62 0.16 30) ≈

// Design tokens — dark
val BgDark         = Color(0xFF0F0F0D)
val SurfaceDark    = Color(0xFF181815)
val Surface2Dark   = Color(0xFF222220)
val Surface3Dark   = Color(0xFF2A2A27)
val InkDark        = Color(0xFFF2F1EC)
val Ink2Dark       = Color(0xFFBDBCB4)
val Ink3Dark       = Color(0xFF7A7970)
val LineDark       = Color(0xFF2A2A27)
val Line2Dark      = Color(0xFF383834)
val AccentDark     = Color(0xFF6AAD52)   // oklch(0.72 0.14 125) ≈
val AccentSoftDark = Color(0xFF182C13)
val AccentDeepDark = Color(0xFF8FCC72)

@Immutable
data class AppColors(
    val bg: Color,
    val surface: Color,
    val surface2: Color,
    val surface3: Color,
    val ink: Color,
    val ink2: Color,
    val ink3: Color,
    val line: Color,
    val line2: Color,
    val accent: Color,
    val accentSoft: Color,
    val accentDeep: Color,
    val accentInk: Color,
    val danger: Color,
    val isDark: Boolean,
)

val LightAppColors = AppColors(
    bg = BgLight, surface = SurfaceLight, surface2 = Surface2Light, surface3 = Surface3Light,
    ink = InkLight, ink2 = Ink2Light, ink3 = Ink3Light,
    line = LineLight, line2 = Line2Light,
    accent = AccentLight, accentSoft = AccentSoft, accentDeep = AccentDeep, accentInk = AccentInk,
    danger = Danger, isDark = false,
)

val DarkAppColors = AppColors(
    bg = BgDark, surface = SurfaceDark, surface2 = Surface2Dark, surface3 = Surface3Dark,
    ink = InkDark, ink2 = Ink2Dark, ink3 = Ink3Dark,
    line = LineDark, line2 = Line2Dark,
    accent = AccentDark, accentSoft = AccentSoftDark, accentDeep = AccentDeepDark, accentInk = AccentInk,
    danger = Danger, isDark = true,
)

val LocalAppColors = staticCompositionLocalOf { LightAppColors }