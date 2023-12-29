package io.helikon.subvt.ui.screen.introduction

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
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

@Composable
fun IntroductionScreen(
    modifier: Modifier = Modifier,
    onUserCreated: () -> Unit,
    viewModel: IntroductionViewModel = viewModel(),
) {
    var launched by rememberSaveable { mutableStateOf(false) }
    val textOffsetAnim by animateDpAsState(
        if (launched) {
            0.dp
        } else {
            dimensionResource(id = R.dimen.text_appear_anim_start_offset)
        },
        animationSpec = tween(
            durationMillis = integerResource(id = R.integer.appear_anim_duration_ms),
            easing = EaseInOutCubic,
        ),
        label = "text_offset_anim"
    )
    val alphaAnim by animateFloatAsState(
        targetValue = if (launched) {
            1f
        } else {
            0f
        },
        animationSpec = tween(
            durationMillis = integerResource(id = R.integer.appear_anim_duration_ms),
            easing = EaseInOutCubic,
        ),
        label = "alpha_anim"
    )
    val actionButtonOffsetAnim by animateDpAsState(
        if (launched) {
            0.dp
        } else {
            dimensionResource(id = R.dimen.action_button_appear_anim_start_offset)
        },
        animationSpec = tween(
            durationMillis = integerResource(id = R.integer.appear_anim_duration_ms),
            easing = EaseInOutCubic,
        ),
        label = "action_button_offset_anim"
    )
    val volumeOffsetAnimation by animateDpAsState(
        if (launched) {
            dimensionResource(id = R.dimen.introduction_icon_volume_end_offset)
        } else {
            dimensionResource(id = R.dimen.introduction_icon_volume_start_offset)
        },
        animationSpec = tween(
            durationMillis = integerResource(id = R.integer.appear_anim_duration_ms),
            delayMillis = integerResource(id = R.integer.introduction_icon_volume_anim_delay_ms),
            easing = EaseInOutCubic,
        ),
        label = "volume_offset_animation"
    )
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
                .offset(volumeOffsetAnimation, -volumeOffsetAnimation)
                .alpha(alphaAnim),
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
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .offset(0.dp, textOffsetAnim)
                    .alpha(alphaAnim),
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.introduction_subtitle_margin_top)))
            Text(
                text = stringResource(R.string.introduction_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .width(dimensionResource(id = R.dimen.introduction_subtitle_width))
                    .offset(0.dp, textOffsetAnim)
                    .alpha(alphaAnim),
            )
            Spacer(modifier = Modifier.weight(1.0f))
            ActionButton(
                text = stringResource(R.string.introduction_get_started),
                isLoading = viewModel.createUserState.value is Loading,
                modifier = Modifier
                    .offset(0.dp, actionButtonOffsetAnim)
                    .alpha(alphaAnim),
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