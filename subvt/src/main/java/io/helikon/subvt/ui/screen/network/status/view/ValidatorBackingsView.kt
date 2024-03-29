package io.helikon.subvt.ui.screen.network.status.view

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.helikon.subvt.R
import io.helikon.subvt.data.model.Network
import io.helikon.subvt.data.preview.PreviewData
import io.helikon.subvt.ui.style.Color
import io.helikon.subvt.ui.style.Font
import io.helikon.subvt.ui.util.ThemePreviews
import io.helikon.subvt.util.formatDecimal
import java.math.BigInteger

@Composable
fun ValidatorBackingsView(
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme(),
    network: Network,
    minStake: BigInteger?,
    averageStake: BigInteger?,
    maxStake: BigInteger?,
) {
    Column(
        modifier =
            modifier
                .clip(shape = RoundedCornerShape(dimensionResource(id = R.dimen.common_panel_border_radius)))
                .background(Color.panelBg(isDark))
                .padding(dimensionResource(id = R.dimen.common_padding)),
    ) {
        Text(
            text = stringResource(id = R.string.network_status_validator_backings),
            textAlign = TextAlign.Center,
            style = Font.light(dimensionResource(id = R.dimen.common_panel_title_font_size).value.sp),
            color = Color.text(isDark),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = stringResource(id = R.string.network_status_validator_backings_minimum),
                    style = Font.light(dimensionResource(id = R.dimen.network_status_panel_subtitle_font_size).value.sp),
                    textAlign = TextAlign.Center,
                    color = Color.text(isDark).copy(alpha = 0.75f),
                )
                Text(
                    text =
                        if (minStake != null) {
                            formatDecimal(
                                if (network.id == 1L) {
                                    minStake
                                } else {
                                    minStake.div(BigInteger("${1_000_000}"))
                                },
                                network.tokenDecimalCount,
                                formatDecimalCount =
                                    if (network.id == 1L) {
                                        0
                                    } else {
                                        4
                                    },
                            )
                        } else {
                            "-"
                        },
                    textAlign = TextAlign.Center,
                    style = Font.semiBold(20.sp),
                    color = Color.text(isDark),
                )
            }
            Text(
                modifier = Modifier.alpha(0.4f),
                text = "/",
                textAlign = TextAlign.Center,
                style = Font.semiBold(20.sp),
                color = Color.text(isDark),
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = stringResource(id = R.string.network_status_validator_backings_average),
                    style = Font.light(dimensionResource(id = R.dimen.network_status_panel_subtitle_font_size).value.sp),
                    textAlign = TextAlign.Center,
                    color = Color.text(isDark).copy(alpha = 0.75f),
                )
                Text(
                    text =
                        if (averageStake != null) {
                            formatDecimal(
                                if (network.id == 1L) {
                                    averageStake
                                } else {
                                    averageStake.div(BigInteger("${1_000_000}"))
                                },
                                network.tokenDecimalCount,
                                formatDecimalCount =
                                    if (network.id == 1L) {
                                        0
                                    } else {
                                        4
                                    },
                            )
                        } else {
                            "-"
                        },
                    textAlign = TextAlign.Center,
                    style = Font.semiBold(20.sp),
                    color = Color.text(isDark),
                )
            }
            Text(
                modifier = Modifier.alpha(0.4f),
                text = "/",
                textAlign = TextAlign.Center,
                style = Font.semiBold(20.sp),
                color = Color.text(isDark),
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = stringResource(id = R.string.network_status_validator_backings_maximum),
                    style = Font.light(dimensionResource(id = R.dimen.network_status_panel_subtitle_font_size).value.sp),
                    textAlign = TextAlign.Center,
                    color = Color.text(isDark).copy(alpha = 0.75f),
                )
                Text(
                    text =
                        if (maxStake != null) {
                            formatDecimal(
                                if (network.id == 1L) {
                                    maxStake
                                } else {
                                    maxStake.div(BigInteger("${1_000_000}"))
                                },
                                network.tokenDecimalCount,
                                formatDecimalCount =
                                    if (network.id == 1L) {
                                        0
                                    } else {
                                        4
                                    },
                            )
                        } else {
                            "-"
                        },
                    color = Color.text(isDark),
                    textAlign = TextAlign.Center,
                    style = Font.semiBold(20.sp),
                )
            }
            Text(
                modifier = Modifier.alpha(0.4f),
                text =
                    if (network.id == 1L) {
                        network.tokenTicker
                    } else {
                        "M${network.tokenTicker}"
                    },
                textAlign = TextAlign.Center,
                style = Font.normal(20.sp),
                color = Color.text(isDark),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@ThemePreviews
@Composable
fun ValidatorBackingsPanelPreview(isDark: Boolean = isSystemInDarkTheme()) {
    Surface(
        color = Color.bg(isDark),
    ) {
        ValidatorBackingsView(
            modifier = Modifier,
            network = PreviewData.networks[0],
            minStake = PreviewData.networkStatus.minStake,
            averageStake = PreviewData.networkStatus.averageStake,
            maxStake = PreviewData.networkStatus.maxStake,
        )
    }
}
