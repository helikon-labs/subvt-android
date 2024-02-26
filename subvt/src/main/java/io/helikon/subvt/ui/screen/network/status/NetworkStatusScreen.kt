package io.helikon.subvt.ui.screen.network.status

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
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
import io.helikon.subvt.ui.component.ServiceStatusIndicator
import io.helikon.subvt.ui.modifier.appear
import io.helikon.subvt.ui.modifier.scrollHeader
import io.helikon.subvt.ui.screen.network.status.view.BlockNumberView
import io.helikon.subvt.ui.screen.network.status.view.EraEpochView
import io.helikon.subvt.ui.screen.network.status.view.EraPointsView
import io.helikon.subvt.ui.screen.network.status.view.LastEraTotalRewardView
import io.helikon.subvt.ui.screen.network.status.view.NetworkSelectorButton
import io.helikon.subvt.ui.screen.network.status.view.NetworkSwitcherView
import io.helikon.subvt.ui.screen.network.status.view.ValidatorBackingsView
import io.helikon.subvt.ui.screen.network.status.view.ValidatorCountPanel
import io.helikon.subvt.ui.style.Color
import io.helikon.subvt.ui.style.Font
import io.helikon.subvt.ui.util.ThemePreviews
import kotlinx.coroutines.launch

data class NetworkStatusScreenState(
    val networks: List<Network>,
    val network: Network,
    val serviceStatus: RPCSubscriptionServiceStatus,
    val networkStatus: NetworkStatus?,
    val activeValidatorCountHistory: List<Int>,
    val inactiveValidatorCountHistory: List<Int>,
)

@Composable
fun NetworkStatusScreen(
    modifier: Modifier = Modifier,
    viewModel: NetworkStatusViewModel = hiltViewModel(),
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onActiveValidatorListButtonClicked: () -> Unit,
    onInactiveValidatorListButtonClicked: () -> Unit,
) {
    val serviceStatus by viewModel.serviceStatus.collectAsStateWithLifecycle()
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
        state =
            NetworkStatusScreenState(
                networks = networks,
                network = viewModel.selectedNetwork,
                serviceStatus = serviceStatus,
                networkStatus = viewModel.networkStatus,
                activeValidatorCountHistory = viewModel.activeValidatorCountList,
                inactiveValidatorCountHistory = viewModel.inactiveValidatorCountList,
            ),
        onChangeNetwork = { network ->
            viewModel.changeNetwork(network)
        },
        onActiveValidatorListButtonClicked = onActiveValidatorListButtonClicked,
        onInactiveValidatorListButtonClicked = onInactiveValidatorListButtonClicked,
    )
}

@Composable
fun NetworkStatusScreenContent(
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme(),
    state: NetworkStatusScreenState,
    onChangeNetwork: (Network) -> Unit,
    onActiveValidatorListButtonClicked: () -> Unit,
    onInactiveValidatorListButtonClicked: () -> Unit,
) {
    var networkSwitcherIsVisible by rememberSaveable {
        mutableStateOf(false)
    }
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val headerFullAlphaScrollAmount =
        remember {
            context.resources.getDimension(R.dimen.common_header_full_alpha_scroll_amount)
        }
    val scrolledRatio = scrollState.value.toFloat() / headerFullAlphaScrollAmount
    Box(
        modifier =
            modifier
                .clipToBounds()
                .fillMaxSize(),
    ) {
        if (networkSwitcherIsVisible) {
            NetworkSwitcherView(
                modifier =
                    Modifier
                        .zIndex(15f)
                        .fillMaxSize(),
                serviceStatus = state.serviceStatus,
                networks = state.networks,
                selectedNetwork = state.network,
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
                    .scrollHeader(
                        isDark = isDark,
                        scrolledRatio = scrolledRatio,
                    ),
        ) {
            Spacer(
                modifier =
                    Modifier
                        .padding(0.dp, dimensionResource(id = R.dimen.common_content_margin_top))
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
                            style = Font.semiBold(22.sp),
                            color = Color.text(isDark),
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        ServiceStatusIndicator(serviceStatus = state.serviceStatus)
                    }
                    Spacer(modifier = Modifier.weight(1.0f))
                    NetworkSelectorButton(
                        network = state.network,
                        isClickable = true,
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
                    modifier =
                        Modifier
                            .weight(1f)
                            .appear(0),
                    title = stringResource(id = R.string.network_status_active_validators),
                    validatorCountHistory = state.activeValidatorCountHistory,
                    validatorCount = state.networkStatus?.activeValidatorCount,
                ) {
                    onActiveValidatorListButtonClicked()
                }
                ValidatorCountPanel(
                    modifier =
                        Modifier
                            .weight(1f)
                            .appear(1),
                    title = stringResource(id = R.string.network_status_inactive_validators),
                    validatorCountHistory = state.inactiveValidatorCountHistory,
                    validatorCount = state.networkStatus?.inactiveValidatorCount,
                ) {
                    onInactiveValidatorListButtonClicked()
                }
            }
            BlockNumberView(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .appear(2),
                title = stringResource(id = R.string.network_status_best_block_number),
                blockNumber = state.networkStatus?.bestBlockNumber,
                displayBlockWave = true,
            )
            BlockNumberView(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .appear(3),
                title = stringResource(id = R.string.network_status_best_block_number),
                blockNumber = state.networkStatus?.finalizedBlockNumber,
                displayBlockWave = false,
            )
            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(
                        dimensionResource(id = R.dimen.common_panel_padding),
                    ),
            ) {
                EraEpochView(
                    modifier =
                        Modifier
                            .weight(1.0f)
                            .appear(4),
                    isEra = true,
                    index = state.networkStatus?.activeEra?.index,
                    startTimestamp = state.networkStatus?.activeEra?.startTimestamp,
                    endTimestamp = state.networkStatus?.activeEra?.endTimestamp,
                )
                EraEpochView(
                    modifier =
                        Modifier
                            .weight(1.0f)
                            .appear(5),
                    isEra = false,
                    index = state.networkStatus?.currentEpoch?.index,
                    startTimestamp = state.networkStatus?.currentEpoch?.startTimestamp,
                    endTimestamp = state.networkStatus?.currentEpoch?.endTimestamp,
                )
            }
            EraPointsView(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .appear(6),
                eraPoints = state.networkStatus?.eraRewardPoints,
            )
            LastEraTotalRewardView(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .appear(7),
                reward = state.networkStatus?.lastEraTotalReward,
                network = state.network,
            )
            ValidatorBackingsView(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .appear(8),
                network = state.network,
                minStake = state.networkStatus?.minStake,
                averageStake = state.networkStatus?.averageStake,
                maxStake = state.networkStatus?.maxStake,
            )

            Spacer(
                modifier =
                    Modifier
                        .navigationBarsPadding()
                        .height(dimensionResource(id = R.dimen.common_scrollable_tab_content_margin_bottom)),
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
            state =
                NetworkStatusScreenState(
                    networks = PreviewData.networks,
                    network = PreviewData.networks[0],
                    serviceStatus = RPCSubscriptionServiceStatus.Subscribed(0L),
                    networkStatus = PreviewData.networkStatus,
                    activeValidatorCountHistory = listOf(),
                    inactiveValidatorCountHistory = listOf(),
                ),
            onChangeNetwork = {},
            onActiveValidatorListButtonClicked = {},
            onInactiveValidatorListButtonClicked = {},
        )
    }
}
