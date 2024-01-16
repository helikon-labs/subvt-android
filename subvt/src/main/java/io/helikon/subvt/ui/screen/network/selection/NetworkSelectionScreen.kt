package io.helikon.subvt.ui.screen.network.selection

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import io.helikon.subvt.R
import io.helikon.subvt.data.DataRequestState
import io.helikon.subvt.data.model.app.Network
import io.helikon.subvt.ui.component.ActionButton
import io.helikon.subvt.ui.component.SnackbarScaffold
import io.helikon.subvt.ui.modifier.appear
import io.helikon.subvt.ui.theme.LightGray
import io.helikon.subvt.ui.theme.SubVTTheme
import io.helikon.subvt.ui.util.ThemePreviews
import timber.log.Timber

@Composable
fun NetworkSelectionScreen(
    modifier: Modifier = Modifier,
    viewModel: NetworkSelectionViewModel = hiltViewModel(),
    onComplete: () -> Unit,
) {
    LaunchedEffect(true) {
        viewModel.getNetworks()
    }
    NetworkSelectionScreenContent(
        modifier,
        isLoading = viewModel.getNetworksState == DataRequestState.Loading,
        snackbarIsVisible = viewModel.getNetworksState is DataRequestState.Error,
        onSnackbarRetry = {
            viewModel.getNetworks()
        },
        onComplete = onComplete,
    )
}

@Composable
private fun NetworkSelectionScreenContent(
    modifier: Modifier,
    isLoading: Boolean,
    snackbarIsVisible: Boolean = false,
    networks: List<Network>? = null,
    selectedNetwork: Network? = null,
    onSnackbarRetry: () -> Unit,
    onComplete: () -> Unit,
) {
    SnackbarScaffold(
        snackbarText = stringResource(id = R.string.network_selection_get_networks_error),
        modifier = modifier,
        snackbarIsVisible = snackbarIsVisible,
        onSnackbarRetry = onSnackbarRetry,
    ) {
        Box {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier =
                        Modifier
                            .width(dimensionResource(id = R.dimen.common_progress_width))
                            .align(Alignment.Center),
                    color = LightGray,
                    trackColor = Color.Transparent,
                )
            }
            if (networks != null) {
                Column(modifier = Modifier.align(Alignment.Center).wrapContentSize()) {
                    for (i in 0..(networks.size / 2)) {
                        Row {
                            for (j in (i * 2)..(i * 2 + 2)) {
                                Timber.i("$i $j")
                            }
                        }
                    }
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier =
                    Modifier
                        .fillMaxSize()
                        .safeContentPadding(),
            ) {
                Spacer(modifier = Modifier.weight(1.0f))
                ActionButton(
                    text = stringResource(R.string.network_selection_go),
                    Modifier.appear(
                        0,
                        isVisible = !isLoading && !snackbarIsVisible,
                        yStart = -dimensionResource(id = R.dimen.appear_anim_start_offset),
                    ),
                    isEnabled = selectedNetwork != null,
                    isLoading = false,
                ) {
                    onComplete()
                }
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.introduction_action_button_margin_bottom)))
            }
        }
    }
}

@ThemePreviews
@Composable
fun NetworkSelectionScreenContentPreview() {
    SubVTTheme {
        Surface(
            color = MaterialTheme.colorScheme.surface,
        ) {
            NetworkSelectionScreenContent(
                modifier = Modifier,
                isLoading = true,
                snackbarIsVisible = false,
                onSnackbarRetry = {},
                onComplete = {},
            )
        }
    }
}
