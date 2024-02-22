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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import dev.romainguy.kotlin.math.Float3
import io.helikon.subvt.R
import io.helikon.subvt.data.DataRequestState
import io.helikon.subvt.data.extension.display
import io.helikon.subvt.data.extension.inactiveNominationTotal
import io.helikon.subvt.data.extension.inactiveNominations
import io.helikon.subvt.data.extension.nominationTotal
import io.helikon.subvt.data.model.Network
import io.helikon.subvt.data.model.app.ValidatorDetails
import io.helikon.subvt.data.model.onekv.OneKVNominatorSummary
import io.helikon.subvt.data.model.substrate.AccountId
import io.helikon.subvt.data.preview.PreviewData
import io.helikon.subvt.data.service.RPCSubscriptionServiceStatus
import io.helikon.subvt.ui.component.AnimatedBackground
import io.helikon.subvt.ui.component.SnackbarScaffold
import io.helikon.subvt.ui.component.SnackbarType
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
import kotlinx.coroutines.delay

data class ValidatorDetailsScreenState(
    val validatorDetailsServiceStatus: RPCSubscriptionServiceStatus,
    val myValidatorsStatus: DataRequestState<String>,
    val addRemoveValidatorStatus: DataRequestState<String>,
    val network: Network?,
    val accountId: AccountId,
    val validator: ValidatorDetails?,
    val showIdenticon: Boolean = true,
    val isMyValidator: Boolean,
    val oneKVNominators: List<OneKVNominatorSummary>,
    val snackbarIsVisible: Boolean,
    val snackbarType: SnackbarType,
    val snackbarTextResourceId: Int,
    val identiconRotation: Float3,
)

@Composable
fun ValidatorDetailsScreen(
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme(),
    viewModel: ValidatorDetailsViewModel = hiltViewModel(),
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val snackbarDuration =
        remember {
            context.resources.getInteger(R.integer.snackbar_medium_display_duration_ms).toLong()
        }
    val serviceStatus by viewModel.validatorDetailsServiceStatus.collectAsStateWithLifecycle()
    var snackbarIsVisible by remember { mutableStateOf(false) }
    var snackbarType by remember { mutableStateOf(SnackbarType.INFO) }
    var snackbarTextResourceId by remember { mutableIntStateOf(R.string.validator_details_validator_added) }
    DisposableEffect(lifecycleOwner) {
        val lifecyceObserver =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_START) {
                    viewModel.subscribe()
                    viewModel.startSensor()
                } else if (event == Lifecycle.Event.ON_STOP) {
                    viewModel.unsubscribe()
                    viewModel.stopSensor()
                }
            }
        lifecycleOwner.lifecycle.addObserver(lifecyceObserver)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(lifecyceObserver)
        }
    }
    LaunchedEffect(Unit) {
        viewModel.getMyValidators()
    }
    LaunchedEffect(viewModel.myValidatorsStatus.value) {
        if (viewModel.myValidatorsStatus.value is DataRequestState.Error) {
            snackbarType = SnackbarType.ERROR
            snackbarTextResourceId = R.string.validator_details_my_validators_fetch_error
            snackbarIsVisible = true
        }
    }
    LaunchedEffect(viewModel.addRemoveValidatorStatus.value) {
        if (viewModel.addRemoveValidatorStatus.value is DataRequestState.Success) {
            snackbarType = SnackbarType.SUCCESS
            snackbarTextResourceId =
                if (viewModel.isMyValidator.value) {
                    R.string.validator_details_validator_added
                } else {
                    R.string.validator_details_validator_removed
                }
            snackbarIsVisible = true
            delay(snackbarDuration)
            snackbarIsVisible = false
        } else if (viewModel.addRemoveValidatorStatus.value is DataRequestState.Error) {
            snackbarType = SnackbarType.ERROR
            snackbarTextResourceId =
                if (viewModel.isMyValidator.value) {
                    R.string.validator_details_validator_remove_error
                } else {
                    R.string.validator_details_validator_add_error
                }
            snackbarIsVisible = true
            delay(snackbarDuration)
            snackbarIsVisible = false
        }
    }
    ValidatorDetailsScreenContent(
        modifier = modifier,
        isDark = isDark,
        state =
            ValidatorDetailsScreenState(
                validatorDetailsServiceStatus = serviceStatus,
                myValidatorsStatus = viewModel.myValidatorsStatus.value,
                addRemoveValidatorStatus = viewModel.addRemoveValidatorStatus.value,
                network = viewModel.network,
                accountId = viewModel.accountId,
                validator = viewModel.validator,
                isMyValidator = viewModel.isMyValidator.value,
                oneKVNominators = viewModel.oneKVNominators,
                snackbarIsVisible = snackbarIsVisible,
                snackbarType = snackbarType,
                snackbarTextResourceId = snackbarTextResourceId,
                identiconRotation = viewModel.orientation,
            ),
        onAddValidator = { viewModel.addValidator() },
        onRemoveValidator = { viewModel.removeValidator() },
        onSnackbarRetry =
            when (viewModel.myValidatorsStatus.value) {
                is DataRequestState.Loading -> {
                    {
                    }
                }

                is DataRequestState.Error -> {
                    {
                        snackbarIsVisible = false
                        viewModel.getMyValidators()
                    }
                }

                else -> {
                    null
                }
            },
        onBack = onBack,
    )
}

@Composable
fun ValidatorDetailsScreenContent(
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme(),
    state: ValidatorDetailsScreenState,
    onAddValidator: () -> Unit,
    onRemoveValidator: () -> Unit,
    onSnackbarRetry: (() -> Unit)?,
    onBack: () -> Unit,
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val headerFullAlphaScrollAmount =
        remember {
            context.resources.getDimension(R.dimen.common_header_full_alpha_scroll_amount)
        }
    val scrolledRatio = scrollState.value.toFloat() / headerFullAlphaScrollAmount
    SnackbarScaffold(
        modifier = modifier.clipToBounds(),
        zIndex = 15.0f,
        type = state.snackbarType,
        text = stringResource(id = state.snackbarTextResourceId),
        snackbarIsVisible = state.snackbarIsVisible,
        onSnackbarRetry = onSnackbarRetry,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
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
                        )
                        .zIndex(20.0f),
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
                    if (state.myValidatorsStatus !is DataRequestState.Error) {
                        Box(
                            modifier =
                                Modifier
                                    .noRippleClickable {
                                        if (state.addRemoveValidatorStatus !is DataRequestState.Loading) {
                                            if (state.isMyValidator) {
                                                onRemoveValidator()
                                            } else {
                                                onAddValidator()
                                            }
                                        }
                                    }
                                    .size(dimensionResource(id = R.dimen.top_small_button_size))
                                    .background(
                                        color = Color.panelBg(isDark),
                                        shape = RoundedCornerShape(12.dp),
                                    ),
                            contentAlignment = Alignment.Center,
                        ) {
                            when (state.myValidatorsStatus) {
                                is DataRequestState.Idle, DataRequestState.Loading -> {
                                    CircularProgressIndicator(
                                        modifier =
                                            Modifier
                                                .size(dimensionResource(id = R.dimen.common_small_progress_size))
                                                .align(Alignment.Center),
                                        strokeWidth = dimensionResource(id = R.dimen.common_small_progress_stroke_width),
                                        color = Color.text(isDark).copy(alpha = 0.85f),
                                        trackColor = Color.transparent(),
                                    )
                                }

                                else -> {
                                    Image(
                                        modifier = Modifier.size(dimensionResource(id = R.dimen.top_small_button_image_size)),
                                        painter =
                                            painterResource(
                                                id =
                                                    if (state.isMyValidator) {
                                                        R.drawable.remove_validator_icon
                                                    } else {
                                                        R.drawable.add_validator_icon
                                                    },
                                            ),
                                        contentDescription = "",
                                    )
                                }
                            }
                        }
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
                        Text(
                            text = stringResource(id = R.string.validator_details_para_validation_icon),
                            style = Font.light(12.sp),
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
                if (state.showIdenticon) {
                    IdenticonView(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(dimensionResource(id = R.dimen.validator_details_identicon_height)),
                        accountId = state.accountId,
                        rotation = state.identiconRotation,
                    )
                }
                IdentityView(validator = state.validator)
                Spacer(modifier = Modifier.height(4.dp))
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
                                validatorStake.nominators.map { nominator ->
                                    Triple(
                                        nominator.account.address,
                                        state.oneKVNominators.count { oneKVNominator ->
                                            oneKVNominator.stashAccountId == nominator.account.id
                                        } > 0,
                                        nominator.stake,
                                    )
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
                        state.validator?.inactiveNominations()?.map { nomination ->
                            Triple(
                                nomination.stashAccount.address,
                                state.oneKVNominators.count { oneKVNominator ->
                                    oneKVNominator.stashAccountId == nomination.stashAccount.id
                                } > 0,
                                nomination.stake.activeAmount,
                            )
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
                                    number =
                                        (
                                            state.validator?.returnRatePerBillion
                                                ?: 0
                                        ).toBigInteger(),
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
                        .zIndex(10.0f)
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
}

@ThemePreviews
@Composable
fun ValidatorDetailsScreenPreview(isDark: Boolean = isSystemInDarkTheme()) {
    Surface(
        color = Color.bg(isDark),
    ) {
        ValidatorDetailsScreenContent(
            modifier = Modifier,
            isDark,
            state =
                ValidatorDetailsScreenState(
                    validatorDetailsServiceStatus = RPCSubscriptionServiceStatus.Connecting,
                    myValidatorsStatus = DataRequestState.Idle,
                    addRemoveValidatorStatus = DataRequestState.Idle,
                    network = PreviewData.networks[0],
                    accountId = PreviewData.stashAccountId,
                    validator = null,
                    showIdenticon = false,
                    isMyValidator = false,
                    oneKVNominators = listOf(),
                    snackbarIsVisible = false,
                    snackbarType = SnackbarType.INFO,
                    snackbarTextResourceId = R.string.validator_details_validator_added,
                    identiconRotation = Float3(0.0f, 0.0f, 0.0f),
                ),
            onAddValidator = {},
            onRemoveValidator = {},
            onSnackbarRetry = null,
            onBack = {},
        )
    }
}
