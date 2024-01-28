package io.helikon.subvt.ui.screen.network.status.panel

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.helikon.subvt.R
import io.helikon.subvt.data.model.Network
import io.helikon.subvt.data.preview.PreviewData
import io.helikon.subvt.data.service.RPCSubscriptionServiceStatus
import io.helikon.subvt.ui.modifier.noRippleClickable
import io.helikon.subvt.ui.style.Font
import io.helikon.subvt.ui.theme.Color
import io.helikon.subvt.ui.util.ThemePreviews

@Composable
fun NetworkSwitcherPanel(
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme(),
    serviceStatus: RPCSubscriptionServiceStatus,
    networks: List<Network>,
    selectedNetwork: Network,
    onChangeNetwork: (Network) -> Unit,
    onClose: () -> Unit,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(Color.networkSelectionOverlayBg(isDark))
                .noRippleClickable { onClose() },
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(id = R.dimen.common_padding), 0.dp),
        ) {
            Spacer(
                modifier =
                    Modifier
                        .padding(0.dp, 24.dp)
                        .statusBarsPadding(),
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.Top) {
                    Text(
                        modifier = Modifier.padding(0.dp),
                        text = stringResource(id = R.string.network_status_title),
                        color = Color.text(isDark),
                        style = Font.semiBold(24.sp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier =
                            Modifier
                                .size(dimensionResource(id = R.dimen.status_indicator_circle_size))
                                .clip(CircleShape)
                                .background(
                                    when (serviceStatus) {
                                        is RPCSubscriptionServiceStatus.Idle -> Color.statusIdle()
                                        is RPCSubscriptionServiceStatus.Connected -> Color.statusWaiting()
                                        is RPCSubscriptionServiceStatus.Error -> Color.statusError()
                                        is RPCSubscriptionServiceStatus.Subscribed -> Color.statusActive()
                                        is RPCSubscriptionServiceStatus.Unsubscribed -> Color.statusWaiting()
                                    },
                                ),
                    )
                }
                Spacer(modifier = Modifier.weight(1.0f))
                NetworkSelectorButton(
                    network = selectedNetwork,
                    isOpen = true,
                    onClick = onClose,
                )
            }
            Spacer(
                modifier =
                    Modifier
                        .padding(0.dp, 4.dp),
            )
            LazyColumn(
                modifier =
                    Modifier
                        .align(Alignment.End)
                        .clip(shape = RoundedCornerShape(12.dp))
                        .background(Color.networkButtonBg(isDark))
                        .width(174.dp),
            ) {
                itemsIndexed(networks) { index, network ->
                    Row(
                        modifier =
                            Modifier
                                .height(49.dp)
                                .padding(12.dp, 0.dp)
                                .noRippleClickable {
                                    onChangeNetwork(network)
                                },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            modifier = Modifier.size(24.dp),
                            painter =
                                painterResource(
                                    when (network.id) {
                                        1L -> R.drawable.kusama_icon
                                        else -> R.drawable.polkadot_icon
                                    },
                                ),
                            contentDescription = "",
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            modifier = Modifier.offset(0.dp, (-1).dp),
                            text = network.display,
                            color = Color.text(isDark),
                            style = Font.normal(16.sp),
                        )
                        Spacer(modifier = Modifier.weight(1.0f))
                        Box(
                            modifier =
                                Modifier
                                    .size(dimensionResource(id = R.dimen.status_indicator_circle_size))
                                    .clip(CircleShape)
                                    .background(
                                        if (network.id == selectedNetwork.id) {
                                            Color.itemListSelectionIndicator()
                                        } else {
                                            Color.transparent()
                                        },
                                    ),
                        )
                    }
                    if (index < networks.lastIndex) {
                        HorizontalDivider(thickness = 1.dp, color = Color.itemListDivider(isDark))
                    }
                }
            }
        }
    }
}

@ThemePreviews
@Composable
fun NetworkSwitcherPanelPreview(isDark: Boolean = isSystemInDarkTheme()) {
    Surface(
        color = Color.bg(isDark),
    ) {
        NetworkSwitcherPanel(
            modifier = Modifier,
            networks = PreviewData.networks,
            selectedNetwork = PreviewData.networks[0],
            serviceStatus = RPCSubscriptionServiceStatus.Idle,
            onChangeNetwork = {},
            onClose = {},
        )
    }
}
