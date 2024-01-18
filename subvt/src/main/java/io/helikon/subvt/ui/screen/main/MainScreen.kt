package io.helikon.subvt.ui.screen.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.helikon.subvt.ui.theme.SubVTTheme
import io.helikon.subvt.ui.util.ThemePreviews

private data class MainScreenState(
    val dummy: Int,
)

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    MainScreenContent(modifier, state = MainScreenState(0))
}

@Composable
private fun MainScreenContent(
    modifier: Modifier = Modifier,
    state: MainScreenState,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Text(text = "Main")
    }
}

@ThemePreviews
@Composable
fun MainScreenContentPreview() {
    SubVTTheme {
        Surface(
            color = MaterialTheme.colorScheme.surface,
        ) {
            MainScreenContent(state = MainScreenState(0))
        }
    }
}
