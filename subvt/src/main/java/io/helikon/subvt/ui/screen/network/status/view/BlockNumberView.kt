package io.helikon.subvt.ui.screen.network.status.view

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
fun BlockNumberView(
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme(),
    title: String,
    blockNumber: Long?,
    displayBlockWave: Boolean,
) {
    Box(
        modifier =
            modifier
                .height(128.dp)
                .clip(shape = RoundedCornerShape(dimensionResource(id = R.dimen.common_panel_border_radius)))
                .background(Color.panelBg(isDark))
                .padding(dimensionResource(id = R.dimen.common_padding)),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = title,
                    textAlign = TextAlign.Center,
                    style = Font.normal(dimensionResource(id = R.dimen.network_status_panel_title_font_size).value.sp),
                    color = Color.text(isDark),
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text =
                        if (blockNumber == null) {
                            "-"
                        } else {
                            "$blockNumber"
                        },
                    style = Font.semiBold(38.sp),
                    color = Color.text(isDark),
                    textAlign = TextAlign.Center,
                )
            }
            Spacer(modifier = Modifier.weight(1.0f))
            if (displayBlockWave) {
                BlockWaveView(isDark = isDark, blockNumber = blockNumber)
            }
            Spacer(modifier = Modifier.width(26.dp))
        }
    }
}

@ThemePreviews
@Composable
fun BlockNumberPanelPreview(isDark: Boolean = isSystemInDarkTheme()) {
    Surface(
        color = Color.bg(isDark),
    ) {
        BlockNumberView(
            modifier = Modifier,
            title = stringResource(id = R.string.network_status_best_block_number),
            blockNumber = 12_345_678,
            displayBlockWave = true,
        )
    }
}
