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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
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
import com.zedalpha.shadowgadgets.compose.clippedShadow
import io.helikon.subvt.R
import io.helikon.subvt.data.model.Network
import io.helikon.subvt.data.model.app.ValidatorSummary
import io.helikon.subvt.data.preview.PreviewData
import io.helikon.subvt.data.service.RPCSubscriptionServiceStatus
import io.helikon.subvt.ui.component.AnimatedBackground
import io.helikon.subvt.ui.component.ServiceStatusIndicator
import io.helikon.subvt.ui.modifier.noRippleClickable
import io.helikon.subvt.ui.screen.network.status.panel.NetworkSelectorButton
import io.helikon.subvt.ui.style.Color
import io.helikon.subvt.ui.style.Font
import io.helikon.subvt.ui.util.ThemePreviews
import kotlin.math.min

data class ValidatorListScreenState(
    val serviceStatus: RPCSubscriptionServiceStatus,
    val network: Network,
    val isActiveValidatorList: Boolean,
    val validators: List<ValidatorSummary>,
    val filter: TextFieldValue,
)

@Composable
fun ValidatorListScreen(
    modifier: Modifier = Modifier,
    viewModel: ValidatorListViewModel = hiltViewModel(),
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    isActiveValidatorList: Boolean,
    onBack: () -> Unit,
) {
    val serviceStatus by viewModel.serviceStatus.collectAsStateWithLifecycle()
    DisposableEffect(lifecycleOwner) {
        // Create an observer that triggers our remembered callbacks
        // for sending analytics events
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_START) {
                    viewModel.subscribe(isActive = isActiveValidatorList)
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
                isActiveValidatorList = isActiveValidatorList,
                validators = viewModel.validators,
                filter = viewModel.filter,
            ),
        onBack = onBack,
        onFilterChange = { newFilter ->
            viewModel.filter = newFilter
            viewModel.sortAndFilter()
        },
    )
}

@Composable
fun ValidatorListScreenContent(
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme(),
    state: ValidatorListScreenState,
    onBack: () -> Unit,
    onFilterChange: (TextFieldValue) -> Unit,
) {
    val context = LocalContext.current
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
    val borderRadius = dimensionResource(id = R.dimen.common_panel_border_radius)
    val clipShape =
        remember {
            RoundedCornerShape(
                0.dp,
                0.dp,
                borderRadius,
                borderRadius,
            )
        }
    val scrollState = rememberLazyListState()
    val scrolledRatio = 0f // scrollState.value.toFloat() / scrollState.maxValue.toFloat()
    val focusManager = LocalFocusManager.current
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .noRippleClickable { focusManager.clearFocus() },
    ) {
        AnimatedBackground(
            modifier = Modifier.fillMaxSize(),
        )
        Column(
            modifier =
                Modifier
                    .background(
                        color =
                            Color
                                .panelBg(isDark)
                                .copy(alpha = min(1f, scrolledRatio * 4)),
                        shape = clipShape,
                    )
                    .fillMaxWidth()
                    .zIndex(10f)
                    .clippedShadow(
                        elevation = (10 * min(1f, scrolledRatio * 4)).dp,
                        shape = clipShape,
                    ),
        ) {
            Spacer(
                modifier =
                    Modifier
                        .padding(0.dp, 24.dp)
                        .statusBarsPadding(),
            )
            Row(
                modifier =
                    Modifier
                        .padding(dimensionResource(id = R.dimen.common_padding), 0.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier =
                            Modifier
                                .size(36.dp)
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.common_padding)),
            ) {
                Row(
                    modifier =
                        Modifier
                            .height(36.dp)
                            .background(
                                color = Color.panelBg(isDark),
                                shape = RoundedCornerShape(12.dp),
                            )
                            .weight(1.0f),
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
                                .copy(color = Color.text(isDark), lineHeight = 36.sp),
                        onValueChange = onFilterChange,
                    )
                    Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.common_panel_padding)))
                }
                Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.common_panel_padding)))
                Box(
                    modifier =
                        Modifier
                            .size(36.dp)
                            .background(
                                color = Color.panelBg(isDark),
                                shape = RoundedCornerShape(12.dp),
                            ),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        modifier = Modifier.size(18.dp),
                        painter = painterResource(id = R.drawable.filter_icon),
                        contentDescription = "",
                    )
                }
            }
        }
        LazyColumn(
            modifier =
                Modifier
                    .statusBarsPadding()
                    .fillMaxSize(),
            state = scrollState,
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.common_panel_padding)),
            contentPadding =
                PaddingValues(
                    dimensionResource(id = R.dimen.common_padding),
                    164.dp,
                    dimensionResource(id = R.dimen.common_padding),
                    64.dp,
                ),
        ) {
            items(state.validators) { validator ->
                ValidatorSummaryView(
                    network = state.network,
                    displayNetworkIcon = false,
                    displayActiveStatus = false,
                    validator = validator,
                )
            }
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
                ),
            onBack = {},
            onFilterChange = {},
        )
    }
}
