package io.helikon.subvt.ui.screen.validator.my

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import io.helikon.subvt.R
import io.helikon.subvt.data.DataRequestState
import io.helikon.subvt.data.model.Network
import io.helikon.subvt.data.model.app.ValidatorSummary
import io.helikon.subvt.data.preview.PreviewData
import io.helikon.subvt.ui.component.SnackbarScaffold
import io.helikon.subvt.ui.component.SnackbarType
import io.helikon.subvt.ui.modifier.noRippleClickable
import io.helikon.subvt.ui.modifier.scrollHeader
import io.helikon.subvt.ui.screen.validator.list.ValidatorSummaryView
import io.helikon.subvt.ui.style.Color
import io.helikon.subvt.ui.style.Font
import io.helikon.subvt.ui.util.ThemePreviews
import kotlinx.coroutines.delay
import kotlin.math.abs

data class MyValidatorsScreenState(
    val dataRequestState: DataRequestState<String>,
    val networks: List<Network>,
    val validators: List<ValidatorSummary>?,
)

private const val REFRESH_PERIOD_MS = 5_000L

@Composable
fun MyValidatorsScreen(
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme(),
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    viewModel: MyValidatorsViewModel = hiltViewModel(),
    onAddValidatorButtonClicked: () -> Unit,
    onSelectValidator: (Network, ValidatorSummary) -> Unit,
) {
    val networks: List<Network>? by viewModel.networks.observeAsState()
    LaunchedEffect(networks) {
        networks?.let {
            viewModel.initReportServices(it)
        }
    }

    var isStopped by rememberSaveable { mutableStateOf(true) }
    DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_STOP) {
                    isStopped = true
                } else if (event == Lifecycle.Event.ON_RESUME) {
                    isStopped = false
                    viewModel.getMyValidators()
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(viewModel.dataRequestState) {
        if (viewModel.dataRequestState is DataRequestState.Success) {
            delay(REFRESH_PERIOD_MS)
            if (!isStopped) {
                viewModel.getMyValidators()
            }
        }
    }

    MyValidatorsScreenContent(
        modifier = modifier,
        isDark = isDark,
        state =
            MyValidatorsScreenState(
                dataRequestState = viewModel.dataRequestState,
                networks = networks ?: listOf(),
                validators = viewModel.validators,
            ),
        onSnackbarRetry = {
            viewModel.getMyValidators()
        },
        onAddValidatorButtonClicked = onAddValidatorButtonClicked,
        onSelectValidator = onSelectValidator,
        onDeleteValidator = { validator ->
            viewModel.removeValidator(validator)
        },
    )
}

@Composable
fun MyValidatorsScreenContent(
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme(),
    state: MyValidatorsScreenState,
    onSnackbarRetry: () -> Unit,
    onAddValidatorButtonClicked: () -> Unit,
    onSelectValidator: (Network, ValidatorSummary) -> Unit,
    onDeleteValidator: (ValidatorSummary) -> Unit,
) {
    val scrollState = rememberLazyListState()
    var scrollOffset by rememberSaveable {
        mutableFloatStateOf(0.0f)
    }
    val context = LocalContext.current
    val headerFullAlphaScrollAmount =
        remember {
            context.resources.getDimension(R.dimen.common_header_full_alpha_scroll_amount)
        }
    val scrolledRatio = abs(scrollOffset) / headerFullAlphaScrollAmount
    SnackbarScaffold(
        modifier =
            modifier
                .clipToBounds()
                .fillMaxSize(),
        snackbarBottomPadding = dimensionResource(id = R.dimen.common_scrollable_tab_content_margin_bottom),
        text = stringResource(id = R.string.my_validators_error),
        type = SnackbarType.ERROR,
        snackbarIsVisible = state.dataRequestState is DataRequestState.Error,
        zIndex = 9.0f,
        onSnackbarRetry = onSnackbarRetry,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier =
                    Modifier
                        .scrollHeader(
                            isDark = isDark,
                            scrolledRatio = scrolledRatio,
                        )
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
                        text = stringResource(id = R.string.my_validators_title),
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
            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .zIndex(5.0f),
                state = scrollState,
                verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.common_panel_padding)),
                contentPadding =
                    PaddingValues(
                        dimensionResource(id = R.dimen.common_padding),
                        0.dp,
                        dimensionResource(id = R.dimen.common_padding),
                        0.dp,
                    ),
            ) {
                item {
                    Spacer(
                        modifier =
                            Modifier
                                .onGloballyPositioned {
                                    scrollOffset = it.positionInParent().y
                                }
                                .padding(0.dp, 24.dp)
                                .statusBarsPadding(),
                    )
                    Text(
                        text = "",
                        style = Font.semiBold(24.sp),
                    )
                    Spacer(
                        modifier =
                            Modifier
                                .padding(0.dp, dimensionResource(id = R.dimen.common_padding)),
                    )
                }
                items(
                    state.validators ?: listOf(),
                    key = {
                        it.accountId.getBytes()
                    },
                ) { validator ->
                    ValidatorSummaryView(
                        network = state.networks.first { it.id == validator.networkId },
                        displayNetworkIcon = true,
                        displayActiveStatus = true,
                        validator = validator,
                        onClick = onSelectValidator,
                        onDelete = onDeleteValidator,
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.my_validators_add_validator_button_height)))
                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.common_panel_padding)))
                    Spacer(
                        modifier =
                            Modifier
                                .navigationBarsPadding()
                                .padding(
                                    0.dp,
                                    0.dp,
                                    0.dp,
                                    dimensionResource(id = R.dimen.common_scrollable_tab_content_margin_bottom),
                                ),
                    )
                }
            }
            if (state.dataRequestState !is DataRequestState.Error && state.validators != null) {
                if (state.validators.isEmpty()) {
                    Text(
                        modifier =
                            Modifier
                                .align(Alignment.Center)
                                .zIndex(5.0f),
                        textAlign = TextAlign.Center,
                        text = stringResource(id = R.string.my_validators_no_validators),
                        style = Font.light(14.sp),
                        color = Color.text(isDark),
                    )
                }
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .zIndex(8.0f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    val buttonTextColor =
                        if (isDark) {
                            Color.green()
                        } else {
                            Color.blue()
                        }
                    Row(
                        modifier =
                            Modifier
                                .noRippleClickable {
                                    onAddValidatorButtonClicked()
                                }
                                .shadow(
                                    elevation = 4.dp,
                                    shape = RoundedCornerShape(16.dp),
                                    spotColor = Color.networkButtonShadow(true).copy(alpha = 0.35f),
                                )
                                .background(
                                    color =
                                        Color
                                            .panelBg(isDark)
                                            .copy(alpha = 1.0f),
                                    shape = RoundedCornerShape(dimensionResource(id = R.dimen.action_button_border_radius)),
                                )
                                .size(
                                    dimensionResource(id = R.dimen.my_validators_add_validator_button_width),
                                    dimensionResource(id = R.dimen.my_validators_add_validator_button_height),
                                ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = stringResource(id = R.string.my_validators_add_validator),
                            style = Font.light(14.sp),
                            color = buttonTextColor,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            modifier = Modifier.offset(0.dp, (-1).dp),
                            text = "+",
                            style = Font.light(20.sp),
                            color = buttonTextColor,
                        )
                    }
                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.common_panel_padding)))
                    Spacer(
                        modifier =
                            Modifier
                                .navigationBarsPadding()
                                .height(dimensionResource(id = R.dimen.common_scrollable_tab_content_margin_bottom)),
                    )
                }
            }
        }
    }
}

@ThemePreviews
@Composable
fun MyValidatorsScreenContentPreview(isDark: Boolean = isSystemInDarkTheme()) {
    Surface(
        color = Color.bg(isDark),
    ) {
        MyValidatorsScreenContent(
            modifier = Modifier,
            state =
                MyValidatorsScreenState(
                    dataRequestState = DataRequestState.Success(""),
                    networks = PreviewData.networks,
                    validators = listOf(PreviewData.validatorSummary),
                ),
            onSnackbarRetry = {},
            onAddValidatorButtonClicked = {},
            onSelectValidator = { _, _ -> },
            onDeleteValidator = { _ -> },
        )
    }
}
