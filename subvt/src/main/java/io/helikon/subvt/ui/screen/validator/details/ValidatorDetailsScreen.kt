package io.helikon.subvt.ui.screen.validator.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.helikon.subvt.R
import io.helikon.subvt.data.extension.hasIdentity
import io.helikon.subvt.data.extension.hasParentIdentity
import io.helikon.subvt.data.extension.identityConfirmed
import io.helikon.subvt.data.extension.identityDisplay
import io.helikon.subvt.data.extension.inactiveNominationTotal
import io.helikon.subvt.data.extension.nominationTotal
import io.helikon.subvt.data.extension.parentIdentityConfirmed
import io.helikon.subvt.data.model.Network
import io.helikon.subvt.data.model.app.ValidatorDetails
import io.helikon.subvt.data.preview.PreviewData
import io.helikon.subvt.data.service.RPCSubscriptionServiceStatus
import io.helikon.subvt.ui.component.AnimatedBackground
import io.helikon.subvt.ui.modifier.noRippleClickable
import io.helikon.subvt.ui.modifier.scrollHeader
import io.helikon.subvt.ui.screen.network.status.view.NetworkSelectorButton
import io.helikon.subvt.ui.style.Color
import io.helikon.subvt.ui.style.Font
import io.helikon.subvt.ui.util.ThemePreviews
import io.helikon.subvt.util.formatDecimal

data class ValidatorDetailsScreenState(
    val serviceStatus: RPCSubscriptionServiceStatus,
    val network: Network?,
    val validator: ValidatorDetails?,
)

@Composable
fun ValidatorDetailsScreen(
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme(),
    viewModel: ValidatorDetailsViewModel = hiltViewModel(),
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onBack: () -> Unit,
) {
    val serviceStatus by viewModel.serviceStatus.collectAsStateWithLifecycle()
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
    ValidatorDetailsScreenContent(
        modifier = modifier,
        isDark = isDark,
        state =
            ValidatorDetailsScreenState(
                serviceStatus = serviceStatus,
                network = viewModel.network,
                validator = viewModel.validator,
            ),
        onBack = onBack,
    )
}

@Composable
fun ValidatorDetailsScreenContent(
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme(),
    state: ValidatorDetailsScreenState,
    onBack: () -> Unit,
) {
    val scrollState = rememberScrollState()
    val scrolledRatio =
        if (scrollState.maxValue == 0) {
            0.0f
        } else {
            scrollState.value.toFloat() / scrollState.maxValue.toFloat() * 4.0f
        }
    Box(
        modifier =
            modifier
                .clipToBounds()
                .fillMaxSize(),
    ) {
        AnimatedBackground(
            modifier =
                Modifier
                    .fillMaxSize()
                    .zIndex(0.0f),
        )
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
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.common_panel_padding)),
                verticalAlignment = Alignment.CenterVertically,
                modifier =
                    Modifier
                        .height(dimensionResource(id = R.dimen.network_selector_button_height))
                        .padding(dimensionResource(id = R.dimen.common_padding), 0.dp),
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(dimensionResource(id = R.dimen.network_selector_button_height))
                            .noRippleClickable {
                                onBack()
                            },
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Image(
                        painterResource(id = R.drawable.arrow_back),
                        contentDescription = stringResource(id = R.string.description_back),
                    )
                }
                Spacer(modifier = Modifier.weight(1.0f))
                state.network?.let { network ->
                    NetworkSelectorButton(
                        network = network,
                        isClickable = false,
                    )
                }
                Box(
                    modifier =
                        Modifier
                            .noRippleClickable {
                                // no-op
                            }
                            .size(dimensionResource(id = R.dimen.top_small_button_size))
                            .background(
                                color = Color.panelBg(isDark),
                                shape = RoundedCornerShape(12.dp),
                            ),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        modifier = Modifier.size(dimensionResource(id = R.dimen.top_small_button_image_size)),
                        painter = painterResource(id = R.drawable.add_validator_icon),
                        contentDescription = "",
                    )
                }
                Box(
                    modifier =
                        Modifier
                            .noRippleClickable {
                                // no-op
                            }
                            .size(dimensionResource(id = R.dimen.top_small_button_size))
                            .background(
                                color = Color.panelBg(isDark),
                                shape = RoundedCornerShape(12.dp),
                            ),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        modifier = Modifier.size(dimensionResource(id = R.dimen.top_small_button_image_size)),
                        painter = painterResource(id = R.drawable.remove_validator_icon),
                        contentDescription = "",
                    )
                }
                Box(
                    modifier =
                        Modifier
                            .noRippleClickable {
                                // no-op
                            }
                            .size(dimensionResource(id = R.dimen.top_small_button_size))
                            .background(
                                color = Color.panelBg(isDark),
                                shape = RoundedCornerShape(12.dp),
                            ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(id = R.string.validator_details_rewards_icon),
                        style = Font.light(16.sp),
                        color = Color.text(isDark),
                    )
                }
                Box(
                    modifier =
                        Modifier
                            .noRippleClickable {
                                // no-op
                            }
                            .size(dimensionResource(id = R.dimen.top_small_button_size))
                            .background(
                                color = Color.panelBg(isDark),
                                shape = RoundedCornerShape(12.dp),
                            ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(id = R.string.validator_details_para_validation_icon),
                        style = Font.light(14.sp),
                        color = Color.text(isDark),
                    )
                }
                Box(
                    modifier =
                        Modifier
                            .noRippleClickable {
                                // no-op
                            }
                            .size(dimensionResource(id = R.dimen.top_small_button_size))
                            .background(
                                color = Color.panelBg(isDark),
                                shape = RoundedCornerShape(12.dp),
                            ),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        modifier = Modifier.size(dimensionResource(id = R.dimen.top_small_button_image_size)),
                        painter = painterResource(id = R.drawable.validator_reports_icon),
                        contentDescription = "",
                    )
                }
            }
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.common_padding)))
        }
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(
                        dimensionResource(id = R.dimen.common_padding),
                        0.dp,
                        dimensionResource(id = R.dimen.common_padding),
                        dimensionResource(id = R.dimen.common_padding),
                    )
                    .zIndex(5f)
                    .verticalScroll(scrollState),
            Arrangement.spacedBy(
                dimensionResource(id = R.dimen.common_panel_padding),
            ),
        ) {
            Spacer(
                modifier =
                    Modifier
                        .padding(
                            PaddingValues(
                                0.dp,
                                dimensionResource(id = R.dimen.validator_details_content_padding_top),
                                0.dp,
                                0.dp,
                            ),
                        )
                        .statusBarsPadding(),
            )
            // content begins here
            Box(
                modifier =
                    Modifier.alpha(0.25f).background(color = Color.blue()).fillMaxWidth()
                        .height(dimensionResource(id = R.dimen.validator_details_identicon_height)),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val identityIcon =
                    when {
                        state.validator?.parentIdentityConfirmed() == true -> R.drawable.parent_identity_confirmed_icon
                        state.validator?.identityConfirmed() == true -> R.drawable.identity_confirmed_icon
                        state.validator?.hasParentIdentity() == true -> R.drawable.parent_identity_not_confirmed_icon
                        state.validator?.hasIdentity() == true -> R.drawable.identity_not_confirmed_icon
                        else -> null
                    }
                identityIcon?.let {
                    Image(
                        modifier = Modifier.requiredSize(24.dp),
                        painter = painterResource(id = it),
                        contentDescription = "",
                    )
                }
                Spacer(modifier = Modifier.requiredWidth(dimensionResource(id = R.dimen.common_panel_padding)))
                Text(
                    textAlign = TextAlign.Center,
                    text = state.validator?.identityDisplay() ?: "-",
                    style = Font.normal(26.sp),
                    color = Color.text(isDark),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Column(
                modifier =
                    Modifier
                        .background(
                            color = Color.panelBg(isDark),
                            shape = RoundedCornerShape(dimensionResource(id = R.dimen.common_panel_border_radius)),
                        )
                        .padding(dimensionResource(id = R.dimen.common_padding))
                        .fillMaxWidth(),
            ) {
                Text(
                    text = "Nomination Total",
                    style = Font.light(12.sp),
                    color = Color.text(isDark),
                )
                Spacer(
                    modifier = Modifier.height(dimensionResource(id = R.dimen.common_data_panel_content_margin_top)),
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.common_panel_padding)),
                ) {
                    Text(
                        text =
                            if (state.validator != null && state.network != null) {
                                formatDecimal(
                                    state.validator.nominationTotal(),
                                    state.network.tokenDecimalCount,
                                )
                            } else {
                                "-"
                            },
                        style = Font.semiBold(28.sp),
                        color = Color.text(isDark),
                    )
                    if (state.network != null) {
                        Text(
                            text = state.network.tokenTicker,
                            style = Font.normal(28.sp),
                            color = Color.text(isDark).copy(alpha = 0.6f),
                        )
                    }
                }
            }
            Column(
                modifier =
                    Modifier
                        .background(
                            color = Color.panelBg(isDark),
                            shape = RoundedCornerShape(dimensionResource(id = R.dimen.common_panel_border_radius)),
                        )
                        .padding(dimensionResource(id = R.dimen.common_padding))
                        .fillMaxWidth(),
            ) {
                Text(
                    text = "Self Stake",
                    style = Font.light(12.sp),
                    color = Color.text(isDark),
                )
                Spacer(
                    modifier = Modifier.height(dimensionResource(id = R.dimen.common_data_panel_content_margin_top)),
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.common_panel_padding)),
                ) {
                    Text(
                        text =
                            if (state.validator != null && state.network != null) {
                                formatDecimal(
                                    state.validator.selfStake.activeAmount,
                                    state.network.tokenDecimalCount,
                                )
                            } else {
                                "-"
                            },
                        style = Font.semiBold(28.sp),
                        color = Color.text(isDark),
                    )
                    if (state.network != null) {
                        Text(
                            text = state.network.tokenTicker,
                            style = Font.normal(28.sp),
                            color = Color.text(isDark).copy(alpha = 0.6f),
                        )
                    }
                }
            }
            Column(
                modifier =
                    Modifier
                        .background(
                            color = Color.panelBg(isDark),
                            shape = RoundedCornerShape(dimensionResource(id = R.dimen.common_panel_border_radius)),
                        )
                        .padding(dimensionResource(id = R.dimen.common_padding))
                        .fillMaxWidth(),
            ) {
                Text(
                    text = "Active Stake",
                    style = Font.light(12.sp),
                    color = Color.text(isDark),
                )
                Spacer(
                    modifier = Modifier.height(dimensionResource(id = R.dimen.common_data_panel_content_margin_top)),
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.common_panel_padding)),
                ) {
                    Text(
                        text =
                            if (state.validator?.validatorStake != null && state.network != null) {
                                formatDecimal(
                                    state.validator.validatorStake!!.totalStake,
                                    state.network.tokenDecimalCount,
                                )
                            } else {
                                "-"
                            },
                        style = Font.semiBold(28.sp),
                        color = Color.text(isDark),
                    )
                    if (state.network != null) {
                        Text(
                            text = state.network.tokenTicker,
                            style = Font.normal(28.sp),
                            color = Color.text(isDark).copy(alpha = 0.6f),
                        )
                    }
                }
            }
            Column(
                modifier =
                    Modifier
                        .background(
                            color = Color.panelBg(isDark),
                            shape = RoundedCornerShape(dimensionResource(id = R.dimen.common_panel_border_radius)),
                        )
                        .padding(dimensionResource(id = R.dimen.common_padding))
                        .fillMaxWidth(),
            ) {
                Text(
                    text = "Inactive Nominations",
                    style = Font.light(12.sp),
                    color = Color.text(isDark),
                )
                Spacer(
                    modifier = Modifier.height(dimensionResource(id = R.dimen.common_data_panel_content_margin_top)),
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.common_panel_padding)),
                ) {
                    val inactiveNominationTotal = state.validator?.inactiveNominationTotal()
                    Text(
                        text =
                            if (inactiveNominationTotal != null && state.network != null) {
                                formatDecimal(
                                    inactiveNominationTotal,
                                    state.network.tokenDecimalCount,
                                )
                            } else {
                                "-"
                            },
                        style = Font.semiBold(28.sp),
                        color = Color.text(isDark),
                    )
                    if (state.network != null) {
                        Text(
                            text = state.network.tokenTicker,
                            style = Font.normal(28.sp),
                            color = Color.text(isDark).copy(alpha = 0.6f),
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(104.dp)
                    .zIndex(7.0f)
                    .align(Alignment.BottomCenter)
                    .background(
                        brush =
                            Brush.verticalGradient(
                                colors =
                                    listOf(
                                        Color.transparent(),
                                        Color
                                            .bg(isDark)
                                            .copy(alpha = 0.85f),
                                        Color.bg(isDark),
                                    ),
                            ),
                    ),
        )
    }
}

@ThemePreviews
@Composable
fun ValidatorCountPanelPreview(isDark: Boolean = isSystemInDarkTheme()) {
    Surface(
        color = Color.bg(isDark),
    ) {
        ValidatorDetailsScreenContent(
            modifier = Modifier,
            isDark,
            state =
                ValidatorDetailsScreenState(
                    serviceStatus = RPCSubscriptionServiceStatus.Connecting,
                    network = PreviewData.networks[0],
                    validator = null,
                ),
            onBack = {},
        )
    }
}
