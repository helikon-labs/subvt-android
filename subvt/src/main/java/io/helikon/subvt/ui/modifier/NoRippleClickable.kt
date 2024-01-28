package io.helikon.subvt.ui.modifier

import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier

inline fun Modifier.noRippleClickable(crossinline onClick: () -> Unit): Modifier =
    this.then(
        clickable(
            indication = null,
            interactionSource = NoRippleInteractionSource(),
        ) {
            onClick()
        },
    )
