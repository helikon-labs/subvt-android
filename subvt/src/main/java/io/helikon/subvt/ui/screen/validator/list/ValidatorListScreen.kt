package io.helikon.subvt.ui.screen.validator.list

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.helikon.subvt.R
import io.helikon.subvt.data.extension.ValidatorFilterOption
import io.helikon.subvt.data.extension.ValidatorSortOption
import io.helikon.subvt.data.model.Network
import io.helikon.subvt.data.model.app.ValidatorSummary
import io.helikon.subvt.data.preview.PreviewData
import io.helikon.subvt.data.service.RPCSubscriptionServiceStatus
import io.helikon.subvt.ui.component.AnimatedBackground
import io.helikon.subvt.ui.component.ServiceStatusIndicator
import io.helikon.subvt.ui.modifier.noRippleClickable
import io.helikon.subvt.ui.modifier.scrollHeader
import io.helikon.subvt.ui.screen.network.status.view.NetworkSelectorButton
import io.helikon.subvt.ui.style.Color
import io.helikon.subvt.ui.style.Font
import io.helikon.subvt.ui.util.ThemePreviews
import kotlinx.coroutines.launch
import kotlin.math.abs

data class ValidatorListScreenState(
    val serviceStatus: RPCSubscriptionServiceStatus,
    val network: Network,
    val isActiveValidatorList: Boolean,
    val validators: List<ValidatorSummary>?,
    val filter: TextFieldValue,
    val sortOption: ValidatorSortOption,
    val filterOptions: Set<ValidatorFilterOption>,
)

@Composable
fun ValidatorListScreen(
    modifier: Modifier = Modifier,
    viewModel: ValidatorListViewModel = hiltViewModel(),
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onSelectValidator: (Network, ValidatorSummary) -> Unit,
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

    ValidatorListScreenContent(
        modifier = modifier,
        state =
            ValidatorListScreenState(
                serviceStatus = serviceStatus,
                network = viewModel.selectedNetwork,
                isActiveValidatorList = viewModel.isActive,
                validators = viewModel.validators,
                filter = viewModel.filter,
                sortOption = viewModel.sortOption,
                filterOptions = viewModel.filterOptions,
            ),
        onBack = onBack,
        onFilterChange = { newFilter ->
            viewModel.filter = newFilter
            viewModel.filterAndSort()
        },
        onSelectSortOption = { sortOption ->
            viewModel.changeSortOption(sortOption)
        },
        onSelectFilterOption = { filterOption ->
            viewModel.toggleFilterOption(filterOption)
        },
        onSelectValidator = onSelectValidator,
    )
}

@Composable
fun ValidatorListScreenContent(
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme(),
    state: ValidatorListScreenState,
    onBack: () -> Unit,
    onFilterChange: (TextFieldValue) -> Unit,
    onSelectSortOption: (ValidatorSortOption) -> Unit,
    onSelectFilterOption: (ValidatorFilterOption) -> Unit,
    onSelectValidator: (Network, ValidatorSummary) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val title =
        remember {
            context.resources.getString(
                if (state.isActiveValidatorList) {
                    R.string.validator_list_active_title
                } else {
                    R.string.validator_list_inactive_title
                },
            )
        }
    val scrollState = rememberLazyListState()
    var scrollOffset by rememberSaveable {
        mutableFloatStateOf(0.0f)
    }
    val headerFullAlphaScrollAmount =
        remember {
            context.resources.getDimension(R.dimen.common_header_full_alpha_scroll_amount)
        }
    val scrolledRatio = abs(scrollOffset) / headerFullAlphaScrollAmount
    val focusManager = LocalFocusManager.current
    var filterSortViewIsVisible by rememberSaveable {
        mutableStateOf(false)
    }
    val filterIsEnabled =
        (state.serviceStatus is RPCSubscriptionServiceStatus.Subscribed) && state.validators != null
    val progressIndicatorIsVisible =
        state.validators == null && state.serviceStatus !is RPCSubscriptionServiceStatus.Error
    Box(
        modifier =
            modifier
                .clipToBounds()
                .fillMaxSize()
                .noRippleClickable { focusManager.clearFocus() },
    ) {
        AnimatedBackground(
            modifier =
                Modifier
                    .fillMaxSize()
                    .zIndex(0.0f),
        )
        if (progressIndicatorIsVisible) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .zIndex(4.0f),
            ) {
                CircularProgressIndicator(
                    modifier =
                        Modifier
                            .size(dimensionResource(id = R.dimen.common_progress_size))
                            .align(Alignment.Center),
                    strokeWidth = dimensionResource(id = R.dimen.common_progress_stroke_width),
                    color = Color.lightGray(),
                    trackColor = Color.transparent(),
                )
            }
        }
        if (filterSortViewIsVisible) {
            ValidatorListFilterSortView(
                modifier = Modifier.zIndex(15.0f),
                isActiveValidatorList = state.isActiveValidatorList,
                sortOption = state.sortOption,
                filterOptions = state.filterOptions,
                onSelectSortOption = {
                    onSelectSortOption(it)
                    scope.launch {
                        scrollState.animateScrollToItem(0)
                    }
                },
                onSelectFilterOption = {
                    onSelectFilterOption(it)
                    scope.launch {
                        scrollState.animateScrollToItem(0)
                    }
                },
                onClose = { filterSortViewIsVisible = false },
            )
        }
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
                        .height(dimensionResource(id = R.dimen.network_selector_button_height))
                        .padding(dimensionResource(id = R.dimen.common_padding), 0.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                    Row(verticalAlignment = Alignment.Top) {
                        Text(
                            modifier = Modifier.padding(0.dp),
                            text = title,
                            style = Font.semiBold(18.sp),
                            color = Color.text(isDark),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        ServiceStatusIndicator(
                            modifier = Modifier.scale(0.75f),
                            serviceStatus = state.serviceStatus,
                        )
                    }
                    Spacer(modifier = Modifier.weight(1.0f))
                    NetworkSelectorButton(
                        network = state.network,
                        isClickable = false,
                    )
                }
                Spacer(modifier = Modifier.weight(1.0f))
            }
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.common_padding)))
            // search & filter
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier =
                    Modifier.padding(
                        dimensionResource(id = R.dimen.common_padding),
                        0.dp,
                    ),
            ) {
                Row(
                    modifier =
                        Modifier
                            .height(dimensionResource(id = R.dimen.top_small_button_size))
                            .background(
                                color = Color.panelBg(isDark),
                                shape = RoundedCornerShape(12.dp),
                            )
                            .weight(1.0f)
                            .alpha(
                                if (filterIsEnabled) {
                                    1.0f
                                } else {
                                    0.5f
                                },
                            ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.common_padding)))
                    Image(
                        modifier = Modifier.size(16.dp),
                        painter = painterResource(id = R.drawable.search_icon),
                        contentDescription = "",
                    )
                    Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.common_panel_padding)))
                    BasicTextField(
                        modifier = Modifier.weight(1.0f),
                        singleLine = true,
                        value = state.filter,
                        cursorBrush = SolidColor(Color.text(isDark)),
                        keyboardOptions =
                            KeyboardOptions(
                                capitalization = KeyboardCapitalization.None,
                                autoCorrect = false,
                                imeAction = ImeAction.Done,
                                keyboardType = KeyboardType.Text,
                            ),
                        textStyle =
                            Font.normal(14.sp)
                                .copy(
                                    color = Color.text(isDark),
                                    lineHeight = 36.sp,
                                    letterSpacing = 0.5.sp,
                                ),
                        onValueChange = onFilterChange,
                        enabled = filterIsEnabled,
                        decorationBox = { innerTextField ->
                            Box {
                                if (state.filter.text.isEmpty()) {
                                    Text(
                                        text = stringResource(id = R.string.search),
                                        style =
                                            Font.normal(14.sp)
                                                .copy(lineHeight = 36.sp, letterSpacing = 0.5.sp),
                                        color = Color.text(isDark).copy(alpha = 0.5f),
                                    )
                                }
                                innerTextField()
                            }
                        },
                    )
                    Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.common_panel_padding)))
                }
                Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.common_panel_padding)))
                Box(
                    modifier =
                        Modifier
                            .noRippleClickable {
                                if (filterIsEnabled) {
                                    filterSortViewIsVisible = true
                                }
                            }
                            .size(dimensionResource(id = R.dimen.top_small_button_size))
                            .background(
                                color = Color.panelBg(isDark),
                                shape = RoundedCornerShape(12.dp),
                            )
                            .alpha(
                                if (filterIsEnabled) {
                                    1.0f
                                } else {
                                    0.5f
                                },
                            ),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        modifier = Modifier.size(dimensionResource(id = R.dimen.top_small_button_image_size)),
                        painter = painterResource(id = R.drawable.filter_icon),
                        contentDescription = "",
                    )
                }
            }
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.common_padding)))
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
                    dimensionResource(id = R.dimen.common_padding),
                ),
        ) {
            item {
                Spacer(
                    modifier =
                        Modifier
                            .onGloballyPositioned {
                                scrollOffset = it.positionInParent().y
                            }
                            .padding(
                                PaddingValues(
                                    0.dp,
                                    dimensionResource(id = R.dimen.validator_list_content_padding_top),
                                    0.dp,
                                    0.dp,
                                ),
                            )
                            .statusBarsPadding(),
                )
            }
            items(
                state.validators ?: listOf(),
                key = {
                    it.accountId.getBytes()
                },
            ) { validator ->
                ValidatorSummaryView(
                    network = state.network,
                    displayNetworkIcon = false,
                    displayActiveStatus = false,
                    validator = validator,
                    onClick = { network, selectedValidator ->
                        onSelectValidator(network, selectedValidator)
                    },
                )
            }
            item {
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
fun ValidatorListScreenContentPreview(isDark: Boolean = isSystemInDarkTheme()) {
    Surface(
        color = Color.bg(isDark),
    ) {
        ValidatorListScreenContent(
            modifier = Modifier,
            state =
                ValidatorListScreenState(
                    serviceStatus = RPCSubscriptionServiceStatus.Subscribed(0L),
                    network = PreviewData.networks[0],
                    isActiveValidatorList = true,
                    validators = listOf(PreviewData.validatorSummary),
                    filter = TextFieldValue(""),
                    sortOption = ValidatorSortOption.IDENTITY,
                    filterOptions = setOf(),
                ),
            onBack = {},
            onFilterChange = {},
            onSelectSortOption = {},
            onSelectFilterOption = {},
            onSelectValidator = { _, _ -> },
        )
    }
}
