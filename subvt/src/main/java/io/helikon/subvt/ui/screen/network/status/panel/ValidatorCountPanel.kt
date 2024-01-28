package io.helikon.subvt.ui.screen.network.status.panel

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.helikon.subvt.R
import io.helikon.subvt.ui.modifier.noRippleClickable
import io.helikon.subvt.ui.style.Color
import io.helikon.subvt.ui.style.Font
import io.helikon.subvt.ui.util.ThemePreviews

@Composable
fun ValidatorCountPanel(
    modifier: Modifier,
    isDark: Boolean = isSystemInDarkTheme(),
    title: String,
    validatorCount: Int?,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            modifier
                .height(128.dp)
                .clip(shape = RoundedCornerShape(dimensionResource(id = R.dimen.common_panel_border_radius)))
                .background(Color.panelBg(isDark))
                .noRippleClickable {
                    onClick()
                }
                .padding(dimensionResource(id = R.dimen.common_padding)),
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = title,
                    style = Font.light(dimensionResource(id = R.dimen.network_status_panel_title_font_size).value.sp),
                    color = Color.text(isDark),
                )
                Spacer(modifier = Modifier.weight(1f))
                Image(
                    painter = painterResource(R.drawable.arrow_right_small),
                    contentDescription = "",
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text =
                    if (validatorCount == null) {
                        "-"
                    } else {
                        "$validatorCount"
                    },
                style = Font.medium(28.sp),
                color = Color.text(isDark),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@ThemePreviews
@Composable
fun ValidatorCountPanelPreview(isDark: Boolean = isSystemInDarkTheme()) {
    Surface(
        color = Color.bg(isDark),
    ) {
        ValidatorCountPanel(
            modifier = Modifier,
            title = stringResource(id = R.string.network_status_active_validators),
            validatorCount = 123,
        ) {
            // no-op
        }
    }
}
