package io.helikon.subvt.ui.component

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.helikon.subvt.R
import io.helikon.subvt.ui.modifier.NoRippleInteractionSource
import io.helikon.subvt.ui.modifier.noRippleClickable
import io.helikon.subvt.ui.theme.SubVTTheme
import io.helikon.subvt.ui.util.ThemePreviews

@Composable
fun SnackbarScaffold(
    modifier: Modifier = Modifier,
    snackbarText: String = "",
    snackbarIsVisible: Boolean = false,
    onSnackbarClick: (() -> Unit)? = null,
    onSnackbarRetry: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Box(
        modifier.fillMaxSize(),
    ) {
        content()
        Snackbar(
            text = snackbarText,
            modifier =
                Modifier
                    .align(Alignment.BottomCenter),
            isVisible = snackbarIsVisible,
            onSnackbarClick,
            onSnackbarRetry,
        )
    }
}

@Composable
fun Snackbar(
    text: String,
    modifier: Modifier = Modifier,
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
                    .background(MaterialTheme.colorScheme.primaryContainer)
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
            Image(
                painter = painterResource(id = R.drawable.snackbar_exclamation_icon),
                contentDescription = stringResource(id = R.string.snackbar_exclamation_icon_description),
            )
            Text(
                text,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Light,
            )
            if (onRetry != null) {
                TextButton(
                    modifier = Modifier.wrapContentSize(),
                    colors =
                        ButtonDefaults.buttonColors(
                            Color.Transparent,
                        ),
                    onClick = onRetry,
                    interactionSource = NoRippleInteractionSource(),
                ) {
                    Text(
                        text = stringResource(id = R.string.retry),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Normal,
                    )
                }
            }
        }
    }
}

@ThemePreviews
@Composable
fun SnackbarPreview() {
    SubVTTheme {
        Snackbar(
            isVisible = true,
            text = stringResource(id = R.string.introduction_user_create_error),
            onRetry = {},
        )
    }
}
