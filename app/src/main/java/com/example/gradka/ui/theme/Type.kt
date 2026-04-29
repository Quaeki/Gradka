package com.example.gradka.ui.theme

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.example.gradka.R

val GoogleFontsProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs,
)

val FrauncesFont = GoogleFont("Fraunces")
val InterFont = GoogleFont("Inter")
val JetBrainsMonoFont = GoogleFont("JetBrains Mono")

val FrauncesFontFamily = FontFamily(
    Font(googleFont = FrauncesFont, fontProvider = GoogleFontsProvider, weight = FontWeight.Normal),
    Font(googleFont = FrauncesFont, fontProvider = GoogleFontsProvider, weight = FontWeight.Medium),
)

val InterFontFamily = FontFamily(
    Font(googleFont = InterFont, fontProvider = GoogleFontsProvider, weight = FontWeight.Normal),
    Font(googleFont = InterFont, fontProvider = GoogleFontsProvider, weight = FontWeight.Medium),
    Font(googleFont = InterFont, fontProvider = GoogleFontsProvider, weight = FontWeight.SemiBold),
    Font(googleFont = InterFont, fontProvider = GoogleFontsProvider, weight = FontWeight.Bold),
)

val JetBrainsMonoFontFamily = FontFamily(
    Font(googleFont = JetBrainsMonoFont, fontProvider = GoogleFontsProvider, weight = FontWeight.Medium),
    Font(googleFont = JetBrainsMonoFont, fontProvider = GoogleFontsProvider, weight = FontWeight.SemiBold),
)