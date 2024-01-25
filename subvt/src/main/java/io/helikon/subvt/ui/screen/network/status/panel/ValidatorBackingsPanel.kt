package io.helikon.subvt.ui.screen.network.status.panel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.helikon.subvt.R
import io.helikon.subvt.data.model.Network
import io.helikon.subvt.data.preview.PreviewData
import io.helikon.subvt.ui.theme.SubVTTheme
import io.helikon.subvt.ui.theme.lexendDecaFamily
import io.helikon.subvt.ui.util.ThemePreviews
import io.helikon.subvt.util.formatDecimal
import java.math.BigInteger

@Composable
fun ValidatorBackingsPanel(
    modifier: Modifier = Modifier,
    network: Network,
    minStake: BigInteger?,
    averageStake: BigInteger?,
    maxStake: BigInteger?,
) {
    Column(
        modifier =
            modifier
                .clip(shape = RoundedCornerShape(dimensionResource(id = R.dimen.common_panel_border_radius)))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(dimensionResource(id = R.dimen.common_padding)),
    ) {
        Text(
            text = stringResource(id = R.string.network_status_validator_backings),
            style = MaterialTheme.typography.bodyMedium,
            fontSize = dimensionResource(id = R.dimen.network_status_panel_title_font_size).value.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column {
                Text(
                    text = stringResource(id = R.string.network_status_validator_backings_minimum),
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = dimensionResource(id = R.dimen.network_status_panel_title_font_size).value.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
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
                    fontFamily = lexendDecaFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                )
            }
            Text(
                modifier = Modifier.alpha(0.4f),
                text = "/",
                fontFamily = lexendDecaFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
            )
            Column {
                Text(
                    text = stringResource(id = R.string.network_status_validator_backings_average),
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = dimensionResource(id = R.dimen.network_status_panel_title_font_size).value.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
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
                    fontFamily = lexendDecaFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                )
            }
            Text(
                modifier = Modifier.alpha(0.4f),
                text = "/",
                fontFamily = lexendDecaFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
            )
            Column {
                Text(
                    text = stringResource(id = R.string.network_status_validator_backings_maximum),
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = dimensionResource(id = R.dimen.network_status_panel_title_font_size).value.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
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
                    fontFamily = lexendDecaFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
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
                fontFamily = lexendDecaFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@ThemePreviews
@Composable
fun ValidatorBackingsPanelPreview() {
    SubVTTheme {
        Surface(
            color = MaterialTheme.colorScheme.surface,
        ) {
            ValidatorBackingsPanel(
                modifier = Modifier,
                network = PreviewData.networks[0],
                minStake = PreviewData.networkStatus.minStake,
                averageStake = PreviewData.networkStatus.averageStake,
                maxStake = PreviewData.networkStatus.maxStake,
            )
        }
    }
}
