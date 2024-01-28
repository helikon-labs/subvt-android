package io.helikon.subvt.ui.style

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import io.helikon.subvt.R
import androidx.compose.ui.text.font.Font as ComposeFont

private val BLACK = ComposeFont(R.font.lexend_deca_black, FontWeight.Black)
private val BOLD = ComposeFont(R.font.lexend_deca_bold, FontWeight.Bold)
private val X_BOLD = ComposeFont(R.font.lexend_deca_extra_bold, FontWeight.ExtraBold)
private val X_LIGHT = ComposeFont(R.font.lexend_deca_extra_light, FontWeight.ExtraLight)
private val LIGHT = ComposeFont(R.font.lexend_deca_light, FontWeight.Light)
private val MEDIUM = ComposeFont(R.font.lexend_deca_medium, FontWeight.Medium)
private val NORMAL = ComposeFont(R.font.lexend_deca_regular, FontWeight.Normal)
private val SEMI_BOLD = ComposeFont(R.font.lexend_deca_semi_bold, FontWeight.SemiBold)
private val THIN = ComposeFont(R.font.lexend_deca_thin, FontWeight.Thin)

private val LEXEND_DECA =
    FontFamily(
        BLACK,
        BOLD,
        X_BOLD,
        X_LIGHT,
        LIGHT,
        MEDIUM,
        NORMAL,
        SEMI_BOLD,
        THIN,
    )

object Font {
    private fun get(
        weight: FontWeight,
        size: TextUnit,
        lineHeight: TextUnit?,
    ) = if (lineHeight != null) {
        TextStyle(
            fontFamily = LEXEND_DECA,
            fontWeight = weight,
            fontSize = size,
            lineHeight = lineHeight,
        )
    } else {
        TextStyle(
            fontFamily = LEXEND_DECA,
            fontWeight = weight,
            fontSize = size,
        )
    }

    fun black(
        size: TextUnit,
        lineHeight: TextUnit? = null,
    ) = get(
        FontWeight.Black,
        size,
        lineHeight,
    )

    fun bold(
        size: TextUnit,
        lineHeight: TextUnit? = null,
    ) = get(
        FontWeight.Bold,
        size,
        lineHeight,
    )

    fun xBold(
        size: TextUnit,
        lineHeight: TextUnit? = null,
    ) = get(
        FontWeight.ExtraBold,
        size,
        lineHeight,
    )

    fun xLight(
        size: TextUnit,
        lineHeight: TextUnit? = null,
    ) = get(
        FontWeight.ExtraLight,
        size,
        lineHeight,
    )

    fun light(
        size: TextUnit,
        lineHeight: TextUnit? = null,
    ) = get(
        FontWeight.Light,
        size,
        lineHeight,
    )

    fun medium(
        size: TextUnit,
        lineHeight: TextUnit? = null,
    ) = get(
        FontWeight.Medium,
        size,
        lineHeight,
    )

    fun normal(
        size: TextUnit,
        lineHeight: TextUnit? = null,
    ) = get(
        FontWeight.Normal,
        size,
        lineHeight,
    )

    fun semiBold(
        size: TextUnit,
        lineHeight: TextUnit? = null,
    ) = get(
        FontWeight.SemiBold,
        size,
        lineHeight,
    )

    fun thin(
        size: TextUnit,
        lineHeight: TextUnit? = null,
    ) = get(
        FontWeight.Thin,
        size,
        lineHeight,
    )
}
