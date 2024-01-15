package io.helikon.subvt.ui.screen.introduction

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.helikon.subvt.R
import io.helikon.subvt.data.DataRequestState
import io.helikon.subvt.ui.component.ActionButton
import io.helikon.subvt.ui.component.SnackbarScaffold
import io.helikon.subvt.ui.modifier.appear
import io.helikon.subvt.ui.theme.SubVTTheme
import io.helikon.subvt.ui.util.ThemePreviews
import kotlinx.coroutines.delay

@Composable
fun IntroductionScreen(
    modifier: Modifier = Modifier,
    viewModel: IntroductionViewModel = viewModel(),
    onUserCreated: () -> Unit,
) {
    val context = LocalContext.current
    var isLaunched by rememberSaveable { mutableStateOf(true) }
    var snackbarIsVisible by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(true) {
        isLaunched = true
    }
    when (viewModel.createUserState) {
        is DataRequestState.Success -> {
            LaunchedEffect(true) {
                onUserCreated()
            }
        }

        is DataRequestState.Error -> {
            LaunchedEffect(true) {
                snackbarIsVisible = true
                delay(
                    timeMillis =
                        context.resources.getInteger(R.integer.snackbar_medium_display_duration_ms)
                            .toLong(),
                )
                snackbarIsVisible = false
            }
        }

        else -> {}
    }
    IntroductionScreenContent(
        modifier,
        isLaunched,
        isLoading = viewModel.createUserState == DataRequestState.Loading,
        snackbarIsVisible,
        onCreateUser = {
            viewModel.createUser(context)
        },
    )
}

@Composable
private fun IntroductionScreenContent(
    modifier: Modifier = Modifier,
    isLaunched: Boolean = false,
    isLoading: Boolean = false,
    snackbarIsVisible: Boolean = false,
    onCreateUser: () -> Unit,
) {
    SnackbarScaffold(
        snackbarText = stringResource(id = R.string.introduction_user_create_error),
        modifier,
        snackbarIsVisible,
        onSnackbarClick = {
            // snackbarIsVisible = false
        },
    ) {
        Box(Modifier.fillMaxHeight()) {
            Image(
                painter = painterResource(id = R.drawable.icon_volume),
                contentDescription = stringResource(id = R.string.introduction_icon_volume_description),
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .appear(
                            0,
                            isVisible = isLaunched,
                            dimensionResource(id = R.dimen.introduction_icon_volume_start_offset),
                            0.dp,
                            -dimensionResource(id = R.dimen.introduction_icon_volume_start_offset),
                            0.dp,
                        ),
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier =
                Modifier
                    .fillMaxSize()
                    .safeContentPadding(),
        ) {
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.introduction_title_margin_top)))
            Text(
                text = stringResource(R.string.introduction_title),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.appear(1, isVisible = isLaunched),
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.introduction_subtitle_margin_top)))
            Text(
                text = stringResource(R.string.introduction_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier =
                    Modifier
                        .width(dimensionResource(id = R.dimen.introduction_subtitle_width))
                        .appear(0, isVisible = isLaunched),
            )
            Spacer(modifier = Modifier.weight(1.0f))
            ActionButton(
                text = stringResource(R.string.introduction_get_started),
                isLoading,
                modifier =
                    Modifier.appear(
                        0,
                        isVisible = isLaunched,
                        0.dp,
                        0.dp,
                        dimensionResource(id = R.dimen.action_button_appear_anim_start_offset),
                        0.dp,
                    ),
            ) {
                onCreateUser()
            }
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.introduction_action_button_margin_bottom)))
        }
    }
}

@ThemePreviews
@Composable
fun IntroductionScreenContentPreview() {
    SubVTTheme {
        Surface(
            color = MaterialTheme.colorScheme.surface,
        ) {
            IntroductionScreenContent(
                modifier = Modifier,
                isLaunched = true,
                isLoading = false,
                snackbarIsVisible = false,
                onCreateUser = {},
            )
        }
    }
}
