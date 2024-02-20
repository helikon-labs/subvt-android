package io.helikon.subvt.ui.component

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import io.helikon.subvt.R
import io.helikon.subvt.ui.modifier.NoRippleInteractionSource
import io.helikon.subvt.ui.modifier.noRippleClickable
import io.helikon.subvt.ui.style.Color
import io.helikon.subvt.ui.style.Font
import io.helikon.subvt.ui.util.ThemePreviews

enum class SnackbarType {
    INFO,
    SUCCESS,
    ERROR,
}

@Composable
fun SnackbarScaffold(
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme(),
    zIndex: Float = 0.0f,
    type: SnackbarType,
    text: String = "",
    snackbarIsVisible: Boolean = false,
    onSnackbarClick: (() -> Unit)? = null,
    onSnackbarRetry: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        content()
        Column(
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .zIndex(zIndex),
        ) {
            Snackbar(
                type = type,
                text = text,
                isDark = isDark,
                isVisible = snackbarIsVisible,
                onClick = onSnackbarClick,
                onRetry = onSnackbarRetry,
            )
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

@Composable
fun Snackbar(
    type: SnackbarType,
    text: String,
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme(),
    isVisible: Boolean = false,
    onClick: (() -> Unit)? = null,
    onRetry: (() -> Unit)? = null,
) {
    val alphaAnim by animateFloatAsState(
        if (isVisible) {
            1f
        } else {
            0f
        },
        animationSpec =
            tween(
                durationMillis = integerResource(id = R.integer.snackbar_anim_duration_ms),
                delayMillis = 0,
                easing = EaseInOutCubic,
            ),
        label = "snackbar_alpha_offset_anim",
    )
    val yOffsetAnim by animateDpAsState(
        if (isVisible) {
            -dimensionResource(id = R.dimen.common_padding)
        } else {
            dimensionResource(id = R.dimen.common_padding).times(10)
        },
        animationSpec =
            tween(
                durationMillis = integerResource(id = R.integer.snackbar_anim_duration_ms),
                delayMillis = 0,
                easing = EaseInOutCubic,
            ),
        label = "snackbar_y_offset_anim",
    )
    Box(
        modifier =
            modifier
                .padding(dimensionResource(id = R.dimen.common_padding), 0.dp)
                .offset(x = 0.dp, y = yOffsetAnim)
                .alpha(alphaAnim),
    ) {
        Row(
            modifier =
                Modifier
                    .clip(shape = RoundedCornerShape(dimensionResource(id = R.dimen.snackbar_border_radius)))
                    .background(Color.snackbarBg(isDark))
                    .padding(
                        dimensionResource(id = R.dimen.snackbar_horizontal_padding),
                        dimensionResource(id = R.dimen.snackbar_vertical_padding),
                    )
                    .noRippleClickable {
                        onClick?.invoke()
                    },
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.snackbar_item_spacing)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            when (type) {
                SnackbarType.SUCCESS -> {
                    Image(
                        modifier = Modifier.size(dimensionResource(id = R.dimen.snackbar_icon_size)),
                        painter = painterResource(id = R.drawable.identity_confirmed_icon),
                        contentDescription = stringResource(id = R.string.snackbar_exclamation_icon_description),
                    )
                }
                SnackbarType.ERROR -> {
                    Image(
                        modifier = Modifier.size(dimensionResource(id = R.dimen.snackbar_icon_size)),
                        painter = painterResource(id = R.drawable.snackbar_exclamation_icon),
                        contentDescription = stringResource(id = R.string.snackbar_exclamation_icon_description),
                    )
                }
                else -> {}
            }
            Text(
                text,
                modifier = Modifier.weight(1f),
                style = Font.light(14.sp, 20.sp),
                color = Color.text(isDark),
            )
            if (type == SnackbarType.ERROR && onRetry != null) {
                TextButton(
                    modifier = Modifier.wrapContentSize(),
                    colors =
                        ButtonDefaults.buttonColors(
                            Color.transparent(),
                        ),
                    onClick = onRetry,
                    interactionSource = NoRippleInteractionSource(),
                ) {
                    Text(
                        text = stringResource(id = R.string.retry),
                        style = Font.normal(14.sp),
                        color = Color.snackbarAction(isDark),
                    )
                }
            }
        }
    }
}

@ThemePreviews
@Composable
fun SnackbarPreview() {
    Snackbar(
        type = SnackbarType.SUCCESS,
        isVisible = true,
        text = stringResource(id = R.string.introduction_user_create_error),
        onRetry = {},
    )
}
