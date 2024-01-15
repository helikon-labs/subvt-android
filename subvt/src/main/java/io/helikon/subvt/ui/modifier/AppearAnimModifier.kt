package io.helikon.subvt.ui.modifier

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.helikon.subvt.R

@Composable
fun Modifier.appear(
    index: Int,
    isVisible: Boolean,
    xStart: Dp = 0.dp,
    xEnd: Dp = 0.dp,
    yStart: Dp = dimensionResource(id = R.dimen.appear_anim_start_offset),
    yEnd: Dp = 0.dp,
): Modifier {
    val animDurationMs = integerResource(id = R.integer.appear_anim_duration_ms)
    val animDelayMs = 250 * index
    val xOffsetAnim by animateDpAsState(
        if (isVisible) {
            xEnd
        } else {
            xStart
        },
        animationSpec =
            tween(
                durationMillis = animDurationMs,
                delayMillis = animDelayMs,
                easing = EaseInOutCubic,
            ),
        label = "x_offset_anim",
    )
    val yOffsetAnim by animateDpAsState(
        if (isVisible) {
            yEnd
        } else {
            yStart
        },
        animationSpec =
            tween(
                durationMillis = animDurationMs,
                delayMillis = animDelayMs,
                easing = EaseInOutCubic,
            ),
        label = "y_offset_anim",
    )
    val alphaAnim by animateFloatAsState(
        targetValue =
            if (isVisible) {
                1f
            } else {
                0f
            },
        animationSpec =
            tween(
                durationMillis = animDurationMs,
                delayMillis = animDelayMs,
                easing = EaseInOutCubic,
            ),
        label = "alpha_anim",
    )
    return this then
        Modifier
            .offset(xOffsetAnim, yOffsetAnim)
            .alpha(alphaAnim)
}
