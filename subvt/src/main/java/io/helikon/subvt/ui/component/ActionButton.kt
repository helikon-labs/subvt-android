package io.helikon.subvt.ui.component

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import io.helikon.subvt.R
import io.helikon.subvt.ui.style.Color
import io.helikon.subvt.ui.style.Font
import io.helikon.subvt.ui.util.ThemePreviews

@Composable
fun ActionButton(
    text: String,
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme(),
    isEnabled: Boolean = true,
    isLoading: Boolean = false,
    onClick: () -> Unit,
) {
    Box(
        modifier
            .height(dimensionResource(id = R.dimen.action_button_height))
            .width(dimensionResource(id = R.dimen.action_button_width)),
        contentAlignment = Alignment.Center,
    ) {
        Button(
            onClick,
            enabled = isEnabled && !isLoading,
            modifier =
                Modifier
                    .height(dimensionResource(id = R.dimen.action_button_height))
                    .width(dimensionResource(id = R.dimen.action_button_width))
                    .alpha(
                        if (!isLoading && !isEnabled) {
                            0.75f
                        } else {
                            1.0f
                        },
                    ),
            shape = RoundedCornerShape(dimensionResource(id = R.dimen.action_button_border_radius)),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = Color.actionButtonBg(),
                    contentColor = Color.actionButtonText(),
                    disabledContainerColor = Color.actionButtonBgDisabled(isDark),
                    disabledContentColor = Color.actionButtonDisabledText(),
                ),
        ) {
            Text(
                text = if (isLoading) "" else text,
                style = Font.semiBold(16.sp),
                color = Color.actionButtonText(),
                textAlign = TextAlign.Center,
            )
        }
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.width(dimensionResource(id = R.dimen.action_button_progress_width)),
                color = Color.actionButtonText(),
                trackColor = Color.transparent(),
            )
        }
    }
}

@ThemePreviews
@Composable
fun ActionButtonPreview() {
    ActionButton(
        text = "Action",
        isEnabled = true,
        isLoading = false,
    ) {
        // no-op
    }
}
