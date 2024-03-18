package io.helikon.subvt.ui.screen.notification.list

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
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
import io.helikon.subvt.data.model.Notification
import io.helikon.subvt.ui.component.SnackbarScaffold
import io.helikon.subvt.ui.component.SnackbarType
import io.helikon.subvt.ui.modifier.noRippleClickable
import io.helikon.subvt.ui.modifier.scrollHeader
import io.helikon.subvt.ui.style.Color
import io.helikon.subvt.ui.style.Font
import kotlin.math.abs

data class NotificationsScreenState(
    val notifications: List<Notification>?,
)

@Composable
fun NotificationsScreen(
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme(),
    viewModel: NotificationsViewModel = hiltViewModel(),
    onSettingsButtonClicked: () -> Unit,
) {
    val notifications: List<Notification>? by viewModel.notifications.observeAsState()
    NotificationsScreenContent(
        modifier = modifier,
        isDark = isDark,
        state =
            NotificationsScreenState(
                notifications = notifications,
            ),
        onSnackbarRetry = {},
        onDeleteButtonClicked = {},
        onSettingsButtonClicked = onSettingsButtonClicked,
    )
}

@Composable
fun NotificationsScreenContent(
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme(),
    state: NotificationsScreenState,
    onSnackbarRetry: () -> Unit,
    onDeleteButtonClicked: () -> Unit,
    onSettingsButtonClicked: () -> Unit,
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
        snackbarIsVisible = false,
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
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.common_panel_padding)),
                ) {
                    Text(
                        modifier =
                            Modifier
                                .padding(0.dp),
                        text = stringResource(id = R.string.notifications_tab_title),
                        style = Font.semiBold(22.sp),
                        color = Color.text(isDark),
                    )
                    Spacer(modifier = Modifier.weight(1.0f))
                    Box(
                        modifier =
                            Modifier
                                .noRippleClickable {
                                    if ((state.notifications?.size ?: 0) > 0) {
                                        onDeleteButtonClicked()
                                    }
                                }
                                .alpha(
                                    if ((state.notifications?.size ?: 0) == 0) {
                                        0.5f
                                    } else {
                                        1.0f
                                    },
                                )
                                .size(dimensionResource(id = R.dimen.top_small_button_size))
                                .background(
                                    color = Color.panelBg(isDark),
                                    shape = RoundedCornerShape(12.dp),
                                ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            modifier = Modifier.size(dimensionResource(id = R.dimen.top_small_button_image_size)),
                            painter =
                                painterResource(id = R.drawable.trash_icon),
                            contentDescription = "",
                        )
                    }
                    Box(
                        modifier =
                            Modifier
                                .noRippleClickable {
                                    onSettingsButtonClicked()
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
                            painter =
                                painterResource(id = R.drawable.settings_icon),
                            contentDescription = "",
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
                // TODO notifications here
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
            if ((state.notifications?.size ?: 0) == 0) {
                Text(
                    modifier =
                        Modifier
                            .align(Alignment.Center)
                            .zIndex(5.0f),
                    textAlign = TextAlign.Center,
                    text = stringResource(id = R.string.notifications_no_notifications),
                    style = Font.light(14.sp),
                    color = Color.text(isDark),
                )
            }
        }
    }
}
