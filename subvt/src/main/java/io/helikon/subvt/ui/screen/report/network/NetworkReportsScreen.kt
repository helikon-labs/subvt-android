package io.helikon.subvt.ui.screen.report.network

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import io.helikon.subvt.R
import io.helikon.subvt.data.DataRequestState
import io.helikon.subvt.data.model.Network
import io.helikon.subvt.ui.component.SnackbarScaffold
import io.helikon.subvt.ui.component.SnackbarType
import io.helikon.subvt.ui.style.Color
import io.helikon.subvt.ui.style.Font

data class NetworkReportsScreenState(
    val dataRequestState: DataRequestState<String>,
    val networks: List<Network>,
)

@Composable
fun NetworkReportsScreen(
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme(),
    viewModel: NetworkReportsViewModel = hiltViewModel(),
    viewReportsButtonClicked: () -> Unit,
) {
    val networks: List<Network>? by viewModel.networks.observeAsState()
    LaunchedEffect(networks) {
        networks?.let {
            viewModel.initReportServices(it)
        }
    }

    NetworkReportsScreenContent(
        modifier = modifier,
        isDark = isDark,
        state =
            NetworkReportsScreenState(
                dataRequestState = viewModel.dataRequestState,
                networks = networks ?: listOf(),
            ),
        onSnackbarRetry = {},
        viewReportsButtonClicked = viewReportsButtonClicked,
    )
}

@Composable
fun NetworkReportsScreenContent(
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme(),
    state: NetworkReportsScreenState,
    onSnackbarRetry: () -> Unit,
    viewReportsButtonClicked: () -> Unit,
) {
    SnackbarScaffold(
        modifier =
            modifier
                .clipToBounds()
                .fillMaxSize(),
        snackbarBottomPadding = dimensionResource(id = R.dimen.common_scrollable_tab_content_margin_bottom),
        text = stringResource(id = R.string.network_reports_error),
        type = SnackbarType.ERROR,
        snackbarIsVisible = state.dataRequestState is DataRequestState.Error,
        zIndex = 9.0f,
        onSnackbarRetry = onSnackbarRetry,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier =
                    Modifier
                        .zIndex(10.0f),
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
                            .padding(dimensionResource(id = R.dimen.common_padding), 0.dp)
                            .height(dimensionResource(id = R.dimen.network_selector_button_height)),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        modifier =
                            Modifier
                                .padding(0.dp),
                        text = stringResource(id = R.string.network_reports_title),
                        style = Font.semiBold(22.sp),
                        color = Color.text(isDark),
                    )
                    Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.common_panel_padding) * 2))
                    if (state.dataRequestState is DataRequestState.Loading) {
                        CircularProgressIndicator(
                            modifier =
                                Modifier
                                    .size(dimensionResource(id = R.dimen.common_small_progress_size)),
                            strokeWidth = dimensionResource(id = R.dimen.common_small_progress_stroke_width),
                            color = Color.text(isDark).copy(alpha = 0.85f),
                            trackColor = Color.transparent(),
                        )
                    }
                }
                Spacer(
                    modifier =
                        Modifier
                            .padding(0.dp, dimensionResource(id = R.dimen.common_panel_padding)),
                )
            }
        }
    }
}
