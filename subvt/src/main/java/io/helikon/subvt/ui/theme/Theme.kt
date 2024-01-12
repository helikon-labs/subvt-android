package io.helikon.subvt.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val darkColorScheme =
    darkColorScheme(
        background = Dark,
        onBackground = Light,
        surface = Dark,
        onSurface = Light,
        primary = Blue,
        onPrimary = Light,
    )

private val lightColorScheme =
    lightColorScheme(
        background = Light,
        onBackground = Dark,
        surface = Light,
        onSurface = Dark,
        primary = Blue,
        onPrimary = Light,
    /*
    secondary = PurpleGrey40,
    tertiary = Pink40,
    onSecondary = Color.White,
    onTertiary = Color.White,
     */
    )

@Composable
fun SubVTTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            darkTheme -> darkColorScheme
            else -> lightColorScheme
        }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
