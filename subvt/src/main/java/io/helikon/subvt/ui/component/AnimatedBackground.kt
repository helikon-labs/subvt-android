package io.helikon.subvt.ui.component

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.helikon.subvt.R
import io.helikon.subvt.ui.style.Color
import io.helikon.subvt.ui.util.ThemePreviews
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class Step {
    START,
    MID,
    END,
}

private fun Step.next() =
    run {
        when (this) {
            Step.START -> Step.MID
            Step.MID -> Step.END
            Step.END -> Step.START
        }
    }

private fun leftScale(step: Step): Pair<Float, Float> =
    when (step) {
        Step.START ->
            Pair(
                3.17f,
                5.39f,
            )

        Step.MID ->
            Pair(
                3.72f,
                5.08f,
            )

        Step.END ->
            Pair(
                5.65f,
                7.05f,
            )
    }

private fun leftOffset(
    isDark: Boolean,
    step: Step,
): Pair<Float, Float> =
    if (isDark) {
        when (step) {
            Step.START ->
                Pair(
                    20.0f,
                    -60.0f,
                )

            Step.MID ->
                Pair(
                    80.0f,
                    -40.0f,
                )

            Step.END ->
                Pair(
                    40.0f,
                    -30.0f,
                )
        }
    } else {
        when (step) {
            Step.START ->
                Pair(
                    -30.0f,
                    -60.0f,
                )

            Step.MID ->
                Pair(
                    10.0f,
                    -40.0f,
                )

            Step.END ->
                Pair(
                    -10.0f,
                    -30.0f,
                )
        }
    }

private fun leftRotation(step: Step): Int =
    when (step) {
        Step.START -> 15
        Step.MID -> -5
        Step.END -> -5
    }

private fun rightScale(
    isDark: Boolean,
    step: Step,
): Pair<Float, Float> =
    if (isDark) {
        when (step) {
            Step.START ->
                Pair(
                    3.68f,
                    4.79f,
                )

            Step.MID ->
                Pair(
                    3.68f,
                    5.08f,
                )

            Step.END ->
                Pair(
                    3.65f,
                    4.79f,
                )
        }
    } else {
        when (step) {
            Step.START ->
                Pair(
                    3.68f,
                    4.79f,
                )

            Step.MID ->
                Pair(
                    3.68f,
                    5.08f,
                )

            Step.END ->
                Pair(
                    3.65f,
                    4.79f,
                )
        }
    }

private fun rightOffset(
    isDark: Boolean,
    step: Step,
): Pair<Float, Float> =
    if (isDark) {
        when (step) {
            Step.START ->
                Pair(
                    20.0f,
                    -60.0f,
                )

            Step.MID ->
                Pair(
                    0.0f,
                    -30.0f,
                )

            Step.END ->
                Pair(
                    30.0f,
                    -50.0f,
                )
        }
    } else {
        when (step) {
            Step.START ->
                Pair(
                    50.0f,
                    -60.0f,
                )

            Step.MID ->
                Pair(
                    30.0f,
                    -30.0f,
                )

            Step.END ->
                Pair(
                    60.0f,
                    -50.0f,
                )
        }
    }

private fun rightRotation(step: Step): Int =
    when (step) {
        Step.START -> -15
        Step.MID -> -12
        Step.END -> -15
    }

@Composable
fun AnimatedBackground(
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme(),
    isActive: Boolean = true,
    isError: Boolean = false,
) {
    val brushLeft =
        remember(isActive || isError) {
            Brush.radialGradient(
                listOf(
                    if (isActive || isError) {
                        Color.bgMorphLeft(isDark = isDark)
                    } else {
                        Color.bgMorphLeftInactive()
                    },
                    Color.transparent(),
                ),
            )
        }
    val brushRight =
        remember(isError) {
            Brush.radialGradient(
                listOf(
                    if (isError) {
                        Color.bgMorphRightError(isDark = isDark)
                    } else if (isActive) {
                        Color.bgMorphRight(isDark = isDark)
                    } else {
                        Color.bgMorphRightInactive()
                    },
                    Color.transparent(),
                ),
            )
        }
    val context = LocalContext.current
    val animDuration = context.resources.getInteger(R.integer.animated_background_anim_duration_ms)
    var step by remember {
        mutableStateOf(Step.START)
    }
    val leftScaleX by animateFloatAsState(
        label = "left_scale_x",
        targetValue =
            leftScale(step).first,
        animationSpec =
            tween(
                durationMillis = animDuration,
                delayMillis = 0,
                easing = EaseInOutCubic,
            ),
        finishedListener = {
            if (isActive) {
                step = step.next()
            }
        },
    )
    val leftScaleY by animateFloatAsState(
        label = "left_scale_x",
        targetValue =
            leftScale(step).second,
        animationSpec =
            tween(
                durationMillis = animDuration,
                delayMillis = 0,
                easing = EaseInOutCubic,
            ),
    )
    val leftOffsetX by animateFloatAsState(
        label = "left_scale_x",
        targetValue =
            leftOffset(isDark, step).first,
        animationSpec =
            tween(
                durationMillis = animDuration,
                delayMillis = 0,
                easing = EaseInOutCubic,
            ),
    )
    val leftOffsetY by animateFloatAsState(
        label = "left_scale_x",
        targetValue =
            leftOffset(isDark, step).second,
        animationSpec =
            tween(
                durationMillis = animDuration,
                delayMillis = 0,
                easing = EaseInOutCubic,
            ),
    )
    val leftRotation by animateIntAsState(
        label = "left_rotation",
        targetValue = leftRotation(step),
        animationSpec =
            tween(
                durationMillis = animDuration,
                delayMillis = 0,
                easing = EaseInOutCubic,
            ),
    )

    val rightScaleX by animateFloatAsState(
        label = "left_scale_x",
        targetValue =
            rightScale(isDark, step).first,
        animationSpec =
            tween(
                durationMillis = animDuration,
                delayMillis = 0,
                easing = EaseInOutCubic,
            ),
    )
    val rightScaleY by animateFloatAsState(
        label = "left_scale_x",
        targetValue =
            rightScale(isDark, step).second,
        animationSpec =
            tween(
                durationMillis = animDuration,
                delayMillis = 0,
                easing = EaseInOutCubic,
            ),
    )
    val rightOffsetX by animateFloatAsState(
        label = "left_scale_x",
        targetValue =
            rightOffset(isDark, step).first,
        animationSpec =
            tween(
                durationMillis = animDuration,
                delayMillis = 0,
                easing = EaseInOutCubic,
            ),
    )
    val rightOffsetY by animateFloatAsState(
        label = "left_scale_x",
        targetValue =
            rightOffset(isDark, step).second,
        animationSpec =
            tween(
                durationMillis = animDuration,
                delayMillis = 0,
                easing = EaseInOutCubic,
            ),
    )
    val rightRotation by animateIntAsState(
        label = "left_rotation",
        targetValue = rightRotation(step),
        animationSpec =
            tween(
                durationMillis = animDuration,
                delayMillis = 0,
                easing = EaseInOutCubic,
            ),
    )

    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        if (isActive) {
            scope.launch(Dispatchers.IO) {
                delay(2000)
                step = step.next()
            }
        }
    }

    Box(modifier = modifier) {
        Box(
            modifier =
                Modifier
                    .offset(leftOffsetX.dp, leftOffsetY.dp)
                    .rotate(leftRotation.toFloat())
                    .scale(leftScaleX, leftScaleY)
                    .size(100.dp, 100.dp)
                    .background(brush = brushLeft),
        )
        Box(
            modifier =
                Modifier
                    .offset(rightOffsetX.dp, rightOffsetY.dp)
                    .align(alignment = Alignment.TopEnd)
                    .rotate(rightRotation.toFloat())
                    .scale(rightScaleX, rightScaleY)
                    .size(100.dp, 100.dp)
                    .background(brush = brushRight),
        )
    }
}

@ThemePreviews
@Composable
fun AnimatedBackgroundPreview(isDark: Boolean = isSystemInDarkTheme()) {
    Surface(
        modifier = Modifier.background(Color.bg(isDark)),
    ) {
        AnimatedBackground(
            modifier = Modifier.fillMaxSize(),
            isActive = true,
            isError = false,
        )
    }
}
