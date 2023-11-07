package io.helikon.subvt.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val darkColorScheme = darkColorScheme(
    surface = Dark,
    onSurface = Light,
    primary = Blue,
    onPrimary = Light,
    /*
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    */
)

private val lightColorScheme = lightColorScheme(
    surface = Light,
    onSurface = Dark,
    primary = Blue,
    onPrimary = Light,
    /*
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
     */

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    */
)

@Composable
fun SubVTTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        /*
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

         */

        darkTheme -> darkColorScheme
        else -> lightColorScheme
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}