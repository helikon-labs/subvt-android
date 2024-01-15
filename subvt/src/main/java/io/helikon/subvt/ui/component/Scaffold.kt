package io.helikon.subvt.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun Scaffold(
    text: String,
    modifier: Modifier = Modifier,
    snackbarIsVisible: Boolean = false,
    onSnackbarClick: (() -> Unit)? = null,
    onSnackbarRetry: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Box(
        modifier,
    ) {
        content()
        Snackbar(
            text,
            modifier =
                Modifier
                    .align(Alignment.BottomCenter),
            isVisible = snackbarIsVisible,
            onSnackbarClick,
            onSnackbarRetry,
        )
    }
}
