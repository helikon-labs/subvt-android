package io.helikon.subvt.ui.screen.introduction

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.helikon.subvt.R
import io.helikon.subvt.ui.component.ActionButton
import io.helikon.subvt.ui.theme.SubVTTheme
import io.helikon.subvt.ui.util.ThemePreviews
import androidx.lifecycle.viewmodel.compose.viewModel
import io.helikon.subvt.data.Loading
import io.helikon.subvt.ui.modifier.appear

@Composable
fun IntroductionScreen(
    modifier: Modifier = Modifier,
    onUserCreated: () -> Unit,
    viewModel: IntroductionViewModel = viewModel(),
) {
    var launched by rememberSaveable { mutableStateOf(false) }
    val userIsCreated: Boolean by viewModel.userIsCreated.collectAsStateWithLifecycle()
    if (userIsCreated) {
        LaunchedEffect(Unit) {
            onUserCreated()
        }
    }
    LaunchedEffect(Unit) {
        launched = true
    }
    Box {
        Image(
            painter = painterResource(id = R.drawable.icon_volume),
            contentDescription = stringResource(id = R.string.introduction_icon_volume_description),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .appear(
                    0,
                    launched,
                    dimensionResource(id = R.dimen.introduction_icon_volume_start_offset),
                    0.dp,
                    -dimensionResource(id = R.dimen.introduction_icon_volume_start_offset),
                    0.dp,
                ),
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxSize()
                .safeContentPadding(),
        ) {
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.introduction_title_margin_top)))
            Text(
                text = stringResource(R.string.introduction_title),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.appear(1, launched),
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.introduction_subtitle_margin_top)))
            Text(
                text = stringResource(R.string.introduction_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .width(dimensionResource(id = R.dimen.introduction_subtitle_width))
                    .appear(0, launched),
            )
            Spacer(modifier = Modifier.weight(1.0f))
            ActionButton(
                text = stringResource(R.string.introduction_get_started),
                isLoading = viewModel.createUserState.value is Loading,
                modifier = Modifier.appear(
                    0,
                    launched,
                    0.dp,
                    0.dp,
                    dimensionResource(id = R.dimen.action_button_appear_anim_start_offset),
                    0.dp,
                ),
            ) {
                viewModel.createUser()
            }
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.introduction_action_button_margin_bottom)))
        }
    }
}

@ThemePreviews
@Composable
fun IntroductionPreview() {
    SubVTTheme {
        Surface(
            color = MaterialTheme.colorScheme.surface,
        ) {
            IntroductionScreen(onUserCreated = {})
        }
    }
}