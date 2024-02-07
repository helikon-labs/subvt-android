package io.helikon.subvt.ui.screen.network.status.view

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.helikon.subvt.R
import io.helikon.subvt.ui.style.Color
import io.helikon.subvt.ui.style.Font
import io.helikon.subvt.ui.util.ThemePreviews

@Composable
fun EraPointsView(
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme(),
    eraPoints: Long?,
) {
    Box(
        modifier =
            modifier
                .clip(shape = RoundedCornerShape(dimensionResource(id = R.dimen.common_panel_border_radius)))
                .background(Color.panelBg(isDark))
                .padding(dimensionResource(id = R.dimen.common_padding)),
    ) {
        Column {
            Text(
                text = stringResource(R.string.network_status_era_points),
                textAlign = TextAlign.Center,
                color = Color.text(isDark),
                style = Font.light(dimensionResource(id = R.dimen.network_status_panel_title_font_size).value.sp),
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text =
                    if (eraPoints == null) {
                        "-"
                    } else {
                        "$eraPoints"
                    },
                textAlign = TextAlign.Center,
                color = Color.text(isDark),
                style = Font.semiBold(20.sp),
            )
        }
    }
}

@ThemePreviews
@Composable
fun EraPointsPanelPreview(isDark: Boolean = isSystemInDarkTheme()) {
    Surface(
        color = Color.bg(isDark),
    ) {
        EraPointsView(
            modifier = Modifier,
            eraPoints = 21_611_532,
        )
    }
}
