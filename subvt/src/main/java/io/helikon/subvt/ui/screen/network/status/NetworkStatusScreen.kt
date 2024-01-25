package io.helikon.subvt.ui.screen.network.status

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.helikon.subvt.R
import io.helikon.subvt.data.model.Network
import io.helikon.subvt.data.model.app.NetworkStatus
import io.helikon.subvt.data.preview.PreviewData
import io.helikon.subvt.data.service.RPCSubscriptionServiceStatus
import io.helikon.subvt.ui.screen.network.status.panel.BlockNumberPanel
import io.helikon.subvt.ui.screen.network.status.panel.EraEpochPanel
import io.helikon.subvt.ui.screen.network.status.panel.EraPointsPanel
import io.helikon.subvt.ui.screen.network.status.panel.LastEraTotalRewardPanel
import io.helikon.subvt.ui.screen.network.status.panel.NetworkSelectorButton
import io.helikon.subvt.ui.screen.network.status.panel.ValidatorBackingsPanel
import io.helikon.subvt.ui.screen.network.status.panel.ValidatorCountPanel
import io.helikon.subvt.ui.theme.Gray
import io.helikon.subvt.ui.theme.Green
import io.helikon.subvt.ui.theme.Orange
import io.helikon.subvt.ui.theme.Red
import io.helikon.subvt.ui.theme.SubVTTheme
import io.helikon.subvt.ui.util.ThemePreviews
import kotlin.math.min

@Composable
fun NetworkStatusScreen(
    modifier: Modifier = Modifier,
    viewModel: NetworkStatusViewModel = hiltViewModel(),
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
) {
    val serviceStatus by viewModel.serviceStatus.collectAsStateWithLifecycle()
    val networkStatus by viewModel.networkStatus.collectAsStateWithLifecycle()

    DisposableEffect(lifecycleOwner) {
        // Create an observer that triggers our remembered callbacks
        // for sending analytics events
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_START) {
                    viewModel.subscribe()
                } else if (event == Lifecycle.Event.ON_STOP) {
                    viewModel.unsubscribe()
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    NetworkStatusScreenContent(modifier, viewModel.selectedNetwork, serviceStatus, networkStatus)
}

@Composable
fun NetworkStatusScreenContent(
    modifier: Modifier = Modifier,
    network: Network,
    serviceStatus: RPCSubscriptionServiceStatus,
    networkStatus: NetworkStatus?,
) {
    val scrollState = rememberScrollState()
    val scrolledRatio = scrollState.value.toFloat() / scrollState.maxValue.toFloat()
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(
                        color =
                            MaterialTheme
                                .colorScheme
                                .primaryContainer
                                .copy(alpha = min(1f, scrolledRatio * 2)),
                        shape =
                            RoundedCornerShape(
                                0.dp,
                                0.dp,
                                dimensionResource(id = R.dimen.common_panel_border_radius),
                                dimensionResource(id = R.dimen.common_panel_border_radius),
                            ),
                    )
                    .zIndex(10f),
        ) {
            Spacer(
                modifier =
                    Modifier
                        .padding(0.dp, 24.dp)
                        .statusBarsPadding(),
            )
            Row(
                modifier =
                    Modifier
                        .padding(dimensionResource(id = R.dimen.common_padding), 0.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.Top) {
                        Text(
                            modifier = Modifier.padding(0.dp),
                            text = stringResource(id = R.string.network_status_title),
                            style = MaterialTheme.typography.headlineLarge,
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier =
                                Modifier
                                    .size(dimensionResource(id = R.dimen.status_indicator_circle_size))
                                    .clip(CircleShape)
                                    .background(
                                        when (serviceStatus) {
                                            is RPCSubscriptionServiceStatus.Idle -> Gray
                                            is RPCSubscriptionServiceStatus.Connected -> Orange
                                            is RPCSubscriptionServiceStatus.Error -> Red
                                            is RPCSubscriptionServiceStatus.Subscribed -> Green
                                            is RPCSubscriptionServiceStatus.Unsubscribed -> Orange
                                        },
                                    ),
                        )
                    }
                    Spacer(modifier = Modifier.weight(1.0f))
                    NetworkSelectorButton(network)
                }
                Spacer(modifier = Modifier.weight(1.0f))
            }
            Spacer(
                modifier =
                    Modifier
                        .padding(0.dp, dimensionResource(id = R.dimen.common_panel_padding)),
            )
        }
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(dimensionResource(id = R.dimen.common_padding), 0.dp)
                    .zIndex(5f)
                    .verticalScroll(scrollState),
            Arrangement.spacedBy(
                dimensionResource(id = R.dimen.common_panel_padding),
            ),
        ) {
            Column(
                modifier =
                    Modifier
                        .padding(0.dp)
                        .alpha(0f),
            ) {
                Spacer(
                    modifier =
                        Modifier
                            .padding(0.dp, 24.dp)
                            .statusBarsPadding(),
                )
                Text(
                    text = "",
                    style = MaterialTheme.typography.headlineLarge,
                )
                Spacer(
                    modifier =
                        Modifier
                            .padding(0.dp, dimensionResource(id = R.dimen.common_padding)),
                )
            }
            // content begins here
            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(
                        dimensionResource(id = R.dimen.common_panel_padding),
                    ),
            ) {
                ValidatorCountPanel(
                    modifier = Modifier.weight(1f),
                    title = stringResource(id = R.string.network_status_active_validators),
                    validatorCount = networkStatus?.activeValidatorCount,
                ) {
                    // no-op
                }
                ValidatorCountPanel(
                    modifier = Modifier.weight(1f),
                    title = stringResource(id = R.string.network_status_inactive_validators),
                    validatorCount = networkStatus?.inactiveValidatorCount,
                ) {
                    // no-op
                }
            }
            BlockNumberPanel(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(id = R.string.network_status_best_block_number),
                blockNumber = networkStatus?.bestBlockNumber,
            )
            BlockNumberPanel(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(id = R.string.network_status_best_block_number),
                blockNumber = networkStatus?.finalizedBlockNumber,
            )
            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(
                        dimensionResource(id = R.dimen.common_panel_padding),
                    ),
            ) {
                EraEpochPanel(
                    modifier = Modifier.weight(1.0f),
                    isEra = true,
                    index = networkStatus?.activeEra?.index,
                    startTimestamp = networkStatus?.activeEra?.startTimestamp,
                    endTimestamp = networkStatus?.activeEra?.endTimestamp,
                )
                EraEpochPanel(
                    modifier = Modifier.weight(1.0f),
                    isEra = false,
                    index = networkStatus?.currentEpoch?.index,
                    startTimestamp = networkStatus?.currentEpoch?.startTimestamp,
                    endTimestamp = networkStatus?.currentEpoch?.endTimestamp,
                )
            }
            EraPointsPanel(
                modifier = Modifier.fillMaxWidth(),
                eraPoints = networkStatus?.eraRewardPoints,
            )
            LastEraTotalRewardPanel(
                modifier = Modifier.fillMaxWidth(),
                reward = networkStatus?.lastEraTotalReward,
                network = network,
            )
            ValidatorBackingsPanel(
                modifier = Modifier.fillMaxWidth(),
                network = network,
                minStake = networkStatus?.minStake,
                averageStake = networkStatus?.averageStake,
                maxStake = networkStatus?.maxStake,
            )

            Spacer(
                modifier =
                    Modifier
                        .navigationBarsPadding()
                        .padding(0.dp, 44.dp),
            )
        }
    }
}

@ThemePreviews
@Composable
fun NetworkStatusScreenContentPreview() {
    SubVTTheme {
        Surface(
            color = MaterialTheme.colorScheme.surface,
        ) {
            NetworkStatusScreenContent(
                modifier = Modifier,
                network = PreviewData.networks[0],
                serviceStatus = RPCSubscriptionServiceStatus.Subscribed(0L),
                networkStatus = PreviewData.networkStatus,
            )
        }
    }
}
