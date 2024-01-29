package io.helikon.subvt.ui.screen.network.status

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import io.helikon.subvt.ui.modifier.appear
import io.helikon.subvt.ui.screen.network.status.panel.BlockNumberPanel
import io.helikon.subvt.ui.screen.network.status.panel.EraEpochPanel
import io.helikon.subvt.ui.screen.network.status.panel.EraPointsPanel
import io.helikon.subvt.ui.screen.network.status.panel.LastEraTotalRewardPanel
import io.helikon.subvt.ui.screen.network.status.panel.NetworkSelectorButton
import io.helikon.subvt.ui.screen.network.status.panel.NetworkSwitcherPanel
import io.helikon.subvt.ui.screen.network.status.panel.ValidatorBackingsPanel
import io.helikon.subvt.ui.screen.network.status.panel.ValidatorCountPanel
import io.helikon.subvt.ui.style.Color
import io.helikon.subvt.ui.style.Font
import io.helikon.subvt.ui.util.ThemePreviews
import kotlinx.coroutines.launch
import kotlin.math.min

@Composable
fun NetworkStatusScreen(
    modifier: Modifier = Modifier,
    viewModel: NetworkStatusViewModel = hiltViewModel(),
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
) {
    val serviceStatus by viewModel.serviceStatus.collectAsStateWithLifecycle()
    val networkStatus by viewModel.networkStatus.collectAsStateWithLifecycle()
    val networks by viewModel.networks.observeAsState(listOf())

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

    NetworkStatusScreenContent(
        modifier = modifier,
        networks = networks,
        network = viewModel.selectedNetwork,
        serviceStatus = serviceStatus,
        networkStatus = networkStatus,
        activeValidatorCountHistory = viewModel.activeValidatorCountList,
        inactiveValidatorCountHistory = viewModel.inactiveValidatorCountList,
        onChangeNetwork = { network ->
            viewModel.changeNetwork(network)
        },
    )
}

@Composable
fun NetworkStatusScreenContent(
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme(),
    networks: List<Network>,
    network: Network,
    serviceStatus: RPCSubscriptionServiceStatus,
    networkStatus: NetworkStatus?,
    activeValidatorCountHistory: List<Int>,
    inactiveValidatorCountHistory: List<Int>,
    onChangeNetwork: (Network) -> Unit,
) {
    var networkSwitcherIsVisible by rememberSaveable {
        mutableStateOf(false)
    }
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val scrolledRatio = scrollState.value.toFloat() / scrollState.maxValue.toFloat()
    Box(modifier = modifier.fillMaxSize()) {
        if (networkSwitcherIsVisible) {
            NetworkSwitcherPanel(
                modifier =
                    Modifier
                        .zIndex(15f)
                        .fillMaxSize(),
                serviceStatus = serviceStatus,
                networks = networks,
                selectedNetwork = network,
                onChangeNetwork = {
                    onChangeNetwork(it)
                    scope.launch {
                        scrollState.animateScrollTo(0)
                    }
                },
                onClose = {
                    networkSwitcherIsVisible = false
                },
            )
        }
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(
                        color =
                            Color
                                .panelBg(isDark)
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
                            style = Font.semiBold(24.sp),
                            color = Color.text(isDark),
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
                        network = network,
                        isOpen = false,
                        onClick = {
                            networkSwitcherIsVisible = true
                        },
                    )
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
                    style = Font.semiBold(24.sp),
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
                    modifier = Modifier.weight(1f).appear(0),
                    title = stringResource(id = R.string.network_status_active_validators),
                    validatorCountHistory = activeValidatorCountHistory,
                    validatorCount = networkStatus?.activeValidatorCount,
                ) {
                    // no-op
                }
                ValidatorCountPanel(
                    modifier = Modifier.weight(1f).appear(1),
                    title = stringResource(id = R.string.network_status_inactive_validators),
                    validatorCountHistory = inactiveValidatorCountHistory,
                    validatorCount = networkStatus?.inactiveValidatorCount,
                ) {
                    // no-op
                }
            }
            BlockNumberPanel(
                modifier = Modifier.fillMaxWidth().appear(2),
                title = stringResource(id = R.string.network_status_best_block_number),
                blockNumber = networkStatus?.bestBlockNumber,
            )
            BlockNumberPanel(
                modifier = Modifier.fillMaxWidth().appear(3),
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
                    modifier = Modifier.weight(1.0f).appear(4),
                    isEra = true,
                    index = networkStatus?.activeEra?.index,
                    startTimestamp = networkStatus?.activeEra?.startTimestamp,
                    endTimestamp = networkStatus?.activeEra?.endTimestamp,
                )
                EraEpochPanel(
                    modifier = Modifier.weight(1.0f).appear(5),
                    isEra = false,
                    index = networkStatus?.currentEpoch?.index,
                    startTimestamp = networkStatus?.currentEpoch?.startTimestamp,
                    endTimestamp = networkStatus?.currentEpoch?.endTimestamp,
                )
            }
            EraPointsPanel(
                modifier = Modifier.fillMaxWidth().appear(6),
                eraPoints = networkStatus?.eraRewardPoints,
            )
            LastEraTotalRewardPanel(
                modifier = Modifier.fillMaxWidth().appear(7),
                reward = networkStatus?.lastEraTotalReward,
                network = network,
            )
            ValidatorBackingsPanel(
                modifier = Modifier.fillMaxWidth().appear(8),
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
fun NetworkStatusScreenContentPreview(isDark: Boolean = isSystemInDarkTheme()) {
    Surface(
        color = Color.bg(isDark),
    ) {
        NetworkStatusScreenContent(
            modifier = Modifier,
            networks = PreviewData.networks,
            network = PreviewData.networks[0],
            serviceStatus = RPCSubscriptionServiceStatus.Subscribed(0L),
            networkStatus = PreviewData.networkStatus,
            activeValidatorCountHistory = listOf(),
            inactiveValidatorCountHistory = listOf(),
            onChangeNetwork = {},
        )
    }
}
