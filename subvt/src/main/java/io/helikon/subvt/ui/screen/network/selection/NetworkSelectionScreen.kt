package io.helikon.subvt.ui.screen.network.selection

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import io.helikon.subvt.R
import io.helikon.subvt.data.DataRequestState
import io.helikon.subvt.data.model.Network
import io.helikon.subvt.data.preview.PreviewData
import io.helikon.subvt.ui.component.ActionButton
import io.helikon.subvt.ui.component.SnackbarScaffold
import io.helikon.subvt.ui.modifier.appear
import io.helikon.subvt.ui.modifier.noRippleClickable
import io.helikon.subvt.ui.style.Color
import io.helikon.subvt.ui.style.Font
import io.helikon.subvt.ui.util.ThemePreviews

private data class NetworkSelectionScreenState(
    val isLoading: Boolean,
    val snackbarIsVisible: Boolean,
    val networks: List<Network>,
    val selectedNetwork: Network?,
)

@Composable
fun NetworkSelectionScreen(
    modifier: Modifier = Modifier,
    viewModel: NetworkSelectionViewModel = hiltViewModel(),
    onComplete: () -> Unit,
) {
    val networks: List<Network>? by viewModel.networks.observeAsState()
    var selectedNetwork: Network? by rememberSaveable {
        mutableStateOf(null)
    }
    LaunchedEffect(networks) {
        viewModel.getNetworks()
    }
    NetworkSelectionScreenContent(
        modifier = modifier,
        state =
            NetworkSelectionScreenState(
                isLoading = viewModel.getNetworksState == DataRequestState.Loading,
                snackbarIsVisible = viewModel.getNetworksState is DataRequestState.Error,
                networks = networks ?: listOf(),
                selectedNetwork = selectedNetwork,
            ),
        onSnackbarRetry = {
            viewModel.getNetworks()
        },
        onSelectNetwork = {
            selectedNetwork = it
        },
        onComplete = {
            viewModel.selectNetwork(selectedNetwork!!)
            onComplete()
        },
    )
}

@Composable
private fun NetworkSelectionScreenContent(
    modifier: Modifier,
    isDark: Boolean = isSystemInDarkTheme(),
    state: NetworkSelectionScreenState,
    onSnackbarRetry: () -> Unit,
    onSelectNetwork: (Network) -> Unit,
    onComplete: () -> Unit,
) {
    SnackbarScaffold(
        snackbarText = stringResource(id = R.string.network_selection_get_networks_error),
        modifier =
            modifier
                .navigationBarsPadding()
                .statusBarsPadding(),
        snackbarIsVisible = state.snackbarIsVisible,
        onSnackbarRetry = onSnackbarRetry,
    ) {
        Box {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier =
                        Modifier
                            .width(dimensionResource(id = R.dimen.common_progress_width))
                            .align(Alignment.Center),
                    color = Color.lightGray(),
                    trackColor = Color.transparent(),
                )
            }
            if (!state.isLoading && state.networks.isNotEmpty()) {
                Column(
                    modifier =
                        Modifier
                            .align(Alignment.TopCenter)
                            .width(264.dp)
                            .offset(0.dp, 120.dp),
                ) {
                    Text(
                        modifier =
                            Modifier.appear(
                                index = 0,
                                yStart = -dimensionResource(id = R.dimen.appear_anim_start_offset),
                            ),
                        text =
                            stringResource(
                                id = R.string.network_selection_title,
                            ),
                        style = Font.semiBold(24.sp),
                        color = Color.text(isDark),
                    )
                    Spacer(Modifier.height(18.dp))
                    Text(
                        modifier =
                            Modifier.appear(
                                index = 1,
                                yStart = -dimensionResource(id = R.dimen.appear_anim_start_offset),
                            ),
                        text =
                            stringResource(
                                id = R.string.network_selection_select_network,
                            ),
                        style = Font.semiBold(14.sp, 20.sp),
                        color = Color.text(isDark),
                    )
                    Spacer(Modifier.height(40.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (i in 0..<state.networks.size / 2) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                for (j in (i * 2)..(i * 2 + 1)) {
                                    val index = i * 2 + j
                                    if (index < state.networks.size) {
                                        val network = state.networks[index]
                                        val isSelectedNetwork =
                                            network.id == (
                                                state.selectedNetwork?.id
                                                    ?: -1
                                            )
                                        Column(
                                            modifier =
                                                Modifier
                                                    .appear(
                                                        index = index + 2,
                                                        yStart = -dimensionResource(id = R.dimen.appear_anim_start_offset),
                                                    )
                                                    .noRippleClickable {
                                                        onSelectNetwork(network)
                                                    }
                                                    .zIndex(if (isSelectedNetwork) 2f else 1f)
                                                    .shadow(
                                                        elevation = if (isSelectedNetwork) 24.dp else 0.dp,
                                                        spotColor = Color.networkButtonShadow(isDark),
                                                    )
                                                    .clip(shape = RoundedCornerShape(12.dp))
                                                    .background(Color.networkButtonBg(isDark))
                                                    .size(128.dp)
                                                    .padding(16.dp),
                                            verticalArrangement = Arrangement.SpaceBetween,
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.Top,
                                            ) {
                                                Image(
                                                    painter =
                                                        painterResource(
                                                            id =
                                                                if (network.id == 1L) {
                                                                    R.drawable.kusama_icon
                                                                } else {
                                                                    R.drawable.polkadot_icon
                                                                },
                                                        ),
                                                    contentDescription = network.display,
                                                    modifier =
                                                        Modifier
                                                            .size(40.dp, 40.dp),
                                                )
                                                if (network.id == (
                                                        state.selectedNetwork?.id
                                                            ?: -1
                                                    )
                                                ) {
                                                    Box(
                                                        modifier =
                                                            Modifier
                                                                .shadow(
                                                                    elevation = if (isSelectedNetwork) 8.dp else 0.dp,
                                                                    ambientColor = Color.itemListSelectionIndicator(),
                                                                    spotColor = Color.itemListSelectionIndicator(),
                                                                    shape = RoundedCornerShape(12.dp),
                                                                )
                                                                .size(dimensionResource(id = R.dimen.status_indicator_circle_size))
                                                                .clip(CircleShape)
                                                                .background(Color.itemListSelectionIndicator())
                                                                .shadow(
                                                                    elevation = 32.dp,
                                                                    shape = CircleShape,
                                                                    ambientColor = Color.itemListSelectionIndicator(),
                                                                    spotColor = Color.itemListSelectionIndicator(),
                                                                ),
                                                    )
                                                }
                                            }
                                            Text(
                                                text = network.display,
                                                style = Font.normal(14.sp),
                                                color = Color.text(isDark),
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize(),
            ) {
                Spacer(modifier = Modifier.weight(1.0f))
                ActionButton(
                    text = stringResource(R.string.network_selection_go),
                    Modifier.appear(
                        0,
                        yStart = -dimensionResource(id = R.dimen.appear_anim_start_offset),
                    ),
                    isEnabled = state.selectedNetwork != null,
                    isLoading = false,
                ) {
                    onComplete()
                }
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.common_padding)))
            }
        }
    }
}

@ThemePreviews
@Composable
fun NetworkSelectionScreenContentPreview(isDark: Boolean = isSystemInDarkTheme()) {
    Surface(
        color = Color.bg(isDark),
    ) {
        NetworkSelectionScreenContent(
            modifier = Modifier,
            state =
                NetworkSelectionScreenState(
                    isLoading = false,
                    snackbarIsVisible = false,
                    networks = PreviewData.networks,
                    selectedNetwork = PreviewData.networks[1],
                ),
            onSnackbarRetry = {},
            onSelectNetwork = {},
            onComplete = {},
        )
    }
}
