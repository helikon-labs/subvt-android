package io.helikon.subvt.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import io.helikon.subvt.R

val lexendDecaFamily =
    FontFamily(
        Font(R.font.lexend_deca_black, FontWeight.Black),
        Font(R.font.lexend_deca_bold, FontWeight.Bold),
        Font(R.font.lexend_deca_extra_bold, FontWeight.ExtraBold),
        Font(R.font.lexend_deca_extra_light, FontWeight.ExtraLight),
        Font(R.font.lexend_deca_light, FontWeight.Light),
        Font(R.font.lexend_deca_medium, FontWeight.Medium),
        Font(R.font.lexend_deca_regular, FontWeight.Normal),
        Font(R.font.lexend_deca_semi_bold, FontWeight.SemiBold),
        Font(R.font.lexend_deca_thin, FontWeight.Thin),
    )

// Set of Material typography styles to start with
val Typography =
    Typography(
        headlineLarge =
            TextStyle(
                fontFamily = lexendDecaFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
            ),
        headlineMedium =
            TextStyle(
                fontFamily = lexendDecaFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 22.sp,
            ),
        bodyMedium =
            TextStyle(
                fontFamily = lexendDecaFamily,
                fontWeight = FontWeight.Light,
                fontSize = 14.sp,
                lineHeight = 22.sp,
            ),
        bodyLarge =
            TextStyle(
                fontFamily = lexendDecaFamily,
                fontWeight = FontWeight.Light,
                fontSize = 18.sp,
            ),
        labelMedium =
            TextStyle(
                fontFamily = lexendDecaFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
            ),
    )
