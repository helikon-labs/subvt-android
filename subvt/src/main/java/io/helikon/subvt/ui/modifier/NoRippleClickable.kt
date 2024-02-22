package io.helikon.subvt.ui.modifier

import androidx.compose.foundation.clickable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import kotlinx.coroutines.delay

inline fun Modifier.noRippleClickable(crossinline onClick: () -> Unit): Modifier {
    return composed {
        var enableAgain by remember { mutableStateOf(true) }
        LaunchedEffect(enableAgain, block = {
            if (enableAgain) return@LaunchedEffect
            delay(timeMillis = 750L)
            enableAgain = true
        })
        this.then(
            clickable(
                indication = null,
                interactionSource = NoRippleInteractionSource(),
            ) {
                if (enableAgain) {
                    enableAgain = false
                    onClick()
                }
            },
        )
    }
}
