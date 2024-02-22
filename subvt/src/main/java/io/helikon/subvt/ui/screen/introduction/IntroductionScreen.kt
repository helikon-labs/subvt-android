package io.helikon.subvt.ui.screen.introduction

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import io.helikon.subvt.R
import io.helikon.subvt.data.DataRequestState
import io.helikon.subvt.ui.component.ActionButton
import io.helikon.subvt.ui.component.AnimatedBackground
import io.helikon.subvt.ui.component.SnackbarScaffold
import io.helikon.subvt.ui.component.SnackbarType
import io.helikon.subvt.ui.modifier.appear
import io.helikon.subvt.ui.style.Color
import io.helikon.subvt.ui.style.Font
import io.helikon.subvt.ui.util.ThemePreviews
import kotlinx.coroutines.delay

private data class IntroductionScreenState(
    val isLoading: Boolean,
    val snackbarIsVisible: Boolean,
)

@Composable
fun IntroductionScreen(
    modifier: Modifier = Modifier,
    viewModel: IntroductionViewModel = hiltViewModel(),
    onUserCreated: () -> Unit,
) {
    val context = LocalContext.current
    var snackbarIsVisible by rememberSaveable { mutableStateOf(false) }
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
        modifier = modifier,
        state =
            IntroductionScreenState(
                isLoading =
                    viewModel.createUserState == DataRequestState.Loading ||
                        viewModel.createUserState is DataRequestState.Success,
                snackbarIsVisible = snackbarIsVisible,
            ),
        onCreateUser = {
            viewModel.createUser()
        },
        onSnackbarClick = {
            snackbarIsVisible = false
        },
    )
}

@Composable
private fun IntroductionScreenContent(
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme(),
    state: IntroductionScreenState,
    onCreateUser: () -> Unit,
    onSnackbarClick: () -> Unit,
) {
    SnackbarScaffold(
        type = SnackbarType.ERROR,
        text = stringResource(id = R.string.introduction_user_create_error),
        modifier =
            modifier
                .navigationBarsPadding()
                .statusBarsPadding(),
        snackbarIsVisible = state.snackbarIsVisible,
        onSnackbarClick = onSnackbarClick,
    ) {
        AnimatedBackground(
            modifier =
                Modifier
                    .fillMaxSize()
                    .zIndex(0.0f),
        )
        Box(
            Modifier.fillMaxHeight(),
        ) {
            Image(
                painter = painterResource(id = R.drawable.icon_volume),
                contentDescription = stringResource(id = R.string.introduction_icon_volume_description),
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .appear(
                            0,
                            dimensionResource(id = R.dimen.introduction_icon_volume_start_offset),
                            0.dp,
                            -dimensionResource(id = R.dimen.introduction_icon_volume_start_offset),
                            0.dp,
                        ),
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize(),
        ) {
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.introduction_title_margin_top)))
            Text(
                text = stringResource(R.string.introduction_title),
                style = Font.semiBold(22.sp),
                color = Color.text(isDark),
                textAlign = TextAlign.Center,
                modifier = Modifier.appear(1),
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.introduction_subtitle_margin_top)))
            Text(
                text = stringResource(R.string.introduction_subtitle),
                style = Font.light(13.sp, 18.sp),
                color = Color.text(isDark),
                textAlign = TextAlign.Center,
                modifier =
                    Modifier
                        .width(dimensionResource(id = R.dimen.introduction_subtitle_width))
                        .appear(0),
            )
            Spacer(modifier = Modifier.weight(1.0f))
            ActionButton(
                text = stringResource(R.string.introduction_get_started),
                modifier =
                    Modifier.appear(
                        0,
                        0.dp,
                        0.dp,
                        dimensionResource(id = R.dimen.action_button_appear_anim_start_offset),
                        0.dp,
                    ),
                isLoading = state.isLoading,
            ) {
                onCreateUser()
            }
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.common_padding)))
        }
    }
}

@ThemePreviews
@Composable
fun IntroductionScreenContentPreview(isDark: Boolean = isSystemInDarkTheme()) {
    Surface(
        color = Color.bg(isDark),
    ) {
        var snackbarIsVisible by rememberSaveable { mutableStateOf(false) }
        IntroductionScreenContent(
            modifier = Modifier,
            state =
                IntroductionScreenState(
                    isLoading = false,
                    snackbarIsVisible = snackbarIsVisible,
                ),
            onCreateUser = {
                snackbarIsVisible = true
            },
            onSnackbarClick = {
                snackbarIsVisible = false
            },
        )
    }
}
