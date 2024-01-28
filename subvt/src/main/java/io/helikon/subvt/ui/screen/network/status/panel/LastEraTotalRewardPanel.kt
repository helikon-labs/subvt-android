package io.helikon.subvt.ui.screen.network.status.panel

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.helikon.subvt.R
import io.helikon.subvt.data.model.Network
import io.helikon.subvt.data.preview.PreviewData
import io.helikon.subvt.ui.style.Font
import io.helikon.subvt.ui.theme.Color
import io.helikon.subvt.ui.util.ThemePreviews
import io.helikon.subvt.util.formatDecimal
import java.math.BigInteger

@Composable
fun LastEraTotalRewardPanel(
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme(),
    reward: BigInteger?,
    network: Network,
) {
    Box(
        modifier =
            modifier
                .height(112.dp)
                .clip(shape = RoundedCornerShape(dimensionResource(id = R.dimen.common_panel_border_radius)))
                .background(Color.panelBg(isDark))
                .padding(dimensionResource(id = R.dimen.common_padding)),
    ) {
        Column {
            Text(
                text = stringResource(id = R.string.network_status_last_era_total_reward),
                textAlign = TextAlign.Center,
                color = Color.text(isDark),
                style = Font.light(dimensionResource(id = R.dimen.network_status_panel_title_font_size).value.sp),
            )
            Spacer(modifier = Modifier.weight(1f))
            Row {
                Text(
                    text =
                        if (reward == null) {
                            "-"
                        } else {
                            formatDecimal(
                                number = reward,
                                tokenDecimalCount = network.tokenDecimalCount,
                            )
                        },
                    textAlign = TextAlign.Center,
                    color = Color.text(isDark),
                    style = Font.semiBold(28.sp),
                )
                Text(
                    modifier = Modifier.alpha(0.6f),
                    text = " ${network.tokenTicker}",
                    textAlign = TextAlign.Center,
                    color = Color.text(isDark),
                    style = Font.normal(28.sp),
                )
            }
        }
    }
}

@ThemePreviews
@Composable
fun LastEraTotalRewardPanelPreview(isDark: Boolean = isSystemInDarkTheme()) {
    Surface(
        color = Color.bg(isDark),
    ) {
        LastEraTotalRewardPanel(
            modifier = Modifier,
            reward = BigInteger("${984_435_511_162_782}"),
            network = PreviewData.networks[0],
        )
    }
}
