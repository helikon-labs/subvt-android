package io.helikon.subvt.ui.screen.network.selection

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.helikon.subvt.R
import io.helikon.subvt.ui.component.ActionButton
import io.helikon.subvt.ui.component.SnackbarScaffold
import io.helikon.subvt.ui.modifier.appear
import io.helikon.subvt.ui.theme.SubVTTheme
import io.helikon.subvt.ui.util.ThemePreviews

@Composable
fun NetworkSelectionScreen(
    modifier: Modifier = Modifier,
    viewModel: NetworkSelectionViewModel = viewModel(),
    onComplete: () -> Unit,
) {
    val snackbarIsVisible by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(true) {
        viewModel.getNetworks()
    }
    NetworkSelectionScreenContent(
        modifier,
        snackbarIsVisible,
        onComplete,
    )
}

@Composable
private fun NetworkSelectionScreenContent(
    modifier: Modifier,
    snackbarIsVisible: Boolean = false,
    onComplete: () -> Unit,
) {
    SnackbarScaffold(
        snackbarText = stringResource(id = R.string.network_selection_get_networks_error),
        modifier,
        snackbarIsVisible,
        onSnackbarClick = {
            // snackbarIsVisible = false
        },
    ) {
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
                isLoading = false,
                modifier =
                    Modifier.appear(
                        0,
                        isVisible = true,
                        0.dp,
                        0.dp,
                        dimensionResource(id = R.dimen.action_button_appear_anim_start_offset),
                        0.dp,
                    ),
            ) {
                onComplete()
            }
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.introduction_action_button_margin_bottom)))
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
                snackbarIsVisible = false,
                onComplete = {},
            )
        }
    }
}
