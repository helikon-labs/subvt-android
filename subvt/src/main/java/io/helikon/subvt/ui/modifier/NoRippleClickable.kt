package io.helikon.subvt.ui.modifier

import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

inline fun Modifier.noRippleClickable(crossinline onClick: () -> Unit): Modifier =
    composed {
        this.then(
            clickable(
                indication = null,
                interactionSource = NoRippleInteractionSource(),
            ) {
                onClick()
            },
        )
    }
