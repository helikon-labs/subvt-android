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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
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
import io.helikon.subvt.data.extension.display
import io.helikon.subvt.data.extension.inactiveNominationTotal
import io.helikon.subvt.data.extension.inactiveNominations
import io.helikon.subvt.data.extension.nominationTotal
import io.helikon.subvt.data.model.Network
import io.helikon.subvt.data.model.app.ValidatorDetails
import io.helikon.subvt.data.preview.PreviewData
import io.helikon.subvt.data.service.RPCSubscriptionServiceStatus
import io.helikon.subvt.ui.component.AnimatedBackground
import io.helikon.subvt.ui.modifier.noRippleClickable
import io.helikon.subvt.ui.modifier.scrollHeader
import io.helikon.subvt.ui.screen.network.status.view.NetworkSelectorButton
import io.helikon.subvt.ui.screen.validator.details.view.AccountAgeView
import io.helikon.subvt.ui.screen.validator.details.view.BalanceView
import io.helikon.subvt.ui.screen.validator.details.view.HorizontalDataView
import io.helikon.subvt.ui.screen.validator.details.view.IconsView
import io.helikon.subvt.ui.screen.validator.details.view.IdenticonView
import io.helikon.subvt.ui.screen.validator.details.view.IdentityView
import io.helikon.subvt.ui.screen.validator.details.view.NominatorListView
import io.helikon.subvt.ui.screen.validator.details.view.OneKVDetailsView
import io.helikon.subvt.ui.screen.validator.details.view.VerticalDataView
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
            state.validator?.let { validator ->
                IdenticonView(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(dimensionResource(id = R.dimen.validator_details_identicon_height)),
                    accountId = validator.account.id,
                )
            }
            IdentityView(validator = state.validator)
            // Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.common_panel_padding) / 2))
            BalanceView(
                titleResourceId = R.string.validator_details_nomination_total,
                network = state.network,
                balance = state.validator?.nominationTotal(),
            )
            BalanceView(
                titleResourceId = R.string.validator_details_self_stake,
                network = state.network,
                balance = state.validator?.selfStake?.activeAmount,
            )
            state.validator?.let { validator ->
                validator.validatorStake?.let { validatorStake ->
                    NominatorListView(
                        titleResourceId = R.string.validator_details_active_stake,
                        network = state.network,
                        count = validatorStake.nominators.size,
                        total = validatorStake.totalStake,
                        nominations =
                            validatorStake.nominators.map {
                                Triple(it.account.address, false, it.stake)
                            }.sortedByDescending {
                                it.third
                            },
                    )
                }
            }
            NominatorListView(
                titleResourceId = R.string.validator_details_inactive_nominations,
                network = state.network,
                count = state.validator?.inactiveNominations()?.size,
                total = state.validator?.inactiveNominationTotal(),
                nominations =
                    state.validator?.inactiveNominations()?.map {
                        Triple(it.stashAccount.address, false, it.stake.activeAmount)
                    }?.sortedByDescending {
                        it.third
                    },
            )
            state.validator?.account?.discoveredAt?.let {
                AccountAgeView(discoveredAt = it)
            }
            HorizontalDataView(
                titleResourceId = R.string.validator_details_offline_faults,
                text = state.validator?.offlineOffenceCount?.toString() ?: "-",
                displayExclamation = (state.validator?.offlineOffenceCount ?: 0) > 0,
            )
            VerticalDataView(
                modifier = Modifier.fillMaxWidth(),
                titleResourceId = R.string.validator_details_reward_destination,
                text =
                    state.validator?.rewardDestination?.display(
                        context = LocalContext.current,
                        prefix = state.network?.ss58Prefix?.toShort() ?: 0,
                    ) ?: "-",
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.common_panel_padding)),
            ) {
                VerticalDataView(
                    modifier = Modifier.weight(1.0f),
                    titleResourceId = R.string.validator_details_commission,
                    text =
                        String.format(
                            stringResource(id = R.string.percentage),
                            formatDecimal(
                                number =
                                    (
                                        state.validator?.preferences?.commissionPerBillion
                                            ?: 0
                                    ).toBigInteger(),
                                tokenDecimalCount = 7,
                                formatDecimalCount = 2,
                            ),
                        ),
                )
                VerticalDataView(
                    modifier = Modifier.weight(1.0f),
                    titleResourceId = R.string.validator_details_apr,
                    text =
                        String.format(
                            stringResource(id = R.string.percentage),
                            formatDecimal(
                                number = (state.validator?.returnRatePerBillion ?: 0).toBigInteger(),
                                tokenDecimalCount = 7,
                                formatDecimalCount = 2,
                            ),
                        ),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.common_panel_padding)),
            ) {
                VerticalDataView(
                    modifier = Modifier.weight(1.0f),
                    titleResourceId = R.string.validator_details_era_blocks,
                    text = state.validator?.blocksAuthored?.toString() ?: "-",
                )
                VerticalDataView(
                    modifier = Modifier.weight(1.0f),
                    titleResourceId = R.string.validator_details_era_points,
                    text = state.validator?.rewardPoints?.toString() ?: "-",
                )
            }
            state.validator?.let { validator ->
                if (validator.onekvCandidateRecordId != null) {
                    OneKVDetailsView(
                        modifier = Modifier.fillMaxWidth(),
                        validator = validator,
                    )
                }
            }
            Spacer(
                modifier =
                    Modifier
                        .navigationBarsPadding()
                        .padding(
                            0.dp,
                            0.dp,
                            0.dp,
                            dimensionResource(id = R.dimen.common_scrollable_content_margin_bottom),
                        ),
            )
        }
        state.validator?.let {
            IconsView(
                Modifier
                    .padding(
                        0.dp,
                        0.dp,
                        0.dp,
                        dimensionResource(id = R.dimen.common_padding),
                    )
                    .navigationBarsPadding()
                    .fillMaxWidth()
                    .zIndex(8.0f)
                    .align(Alignment.BottomCenter),
                validator = it,
            )
        }
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(id = R.dimen.common_bottom_gradient_height))
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
