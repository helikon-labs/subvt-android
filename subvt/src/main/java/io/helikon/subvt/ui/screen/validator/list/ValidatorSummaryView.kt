package io.helikon.subvt.ui.screen.validator.list

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.helikon.subvt.R
import io.helikon.subvt.data.extension.identityDisplay
import io.helikon.subvt.data.model.Network
import io.helikon.subvt.data.model.app.ValidatorSummary
import io.helikon.subvt.data.preview.PreviewData
import io.helikon.subvt.ui.modifier.noRippleClickable
import io.helikon.subvt.ui.style.Color
import io.helikon.subvt.ui.style.Font
import io.helikon.subvt.ui.util.ThemePreviews
import io.helikon.subvt.util.formatDecimal
import kotlin.math.roundToInt

private fun nominationsDisplay(
    network: Network,
    validator: ValidatorSummary,
): String {
    val inactive =
        formatDecimal(
            number = validator.inactiveNominations.totalAmount,
            tokenDecimalCount = network.tokenDecimalCount,
        )
    val inactiveCount = validator.inactiveNominations.nominationCount
    return if (validator.isActive && validator.validatorStake != null) {
        val active =
            formatDecimal(
                number = validator.validatorStake!!.totalStake,
                tokenDecimalCount = network.tokenDecimalCount,
            )
        val activeCount = validator.validatorStake!!.nominatorCount
        "($activeCount) $active / ($inactiveCount) $inactive"
    } else {
        "($inactiveCount) $inactive"
    }
}

private enum class DragAnchors {
    Center,
    End,
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ValidatorSummaryView(
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme(),
    network: Network,
    displayNetworkIcon: Boolean,
    displayActiveStatus: Boolean,
    validator: ValidatorSummary,
    onClick: (Network, ValidatorSummary) -> Unit,
    onDelete: ((ValidatorSummary) -> Unit)? = null,
) {
    val density = LocalDensity.current
    val deleteActionSize = dimensionResource(id = R.dimen.my_validators_delete_action_container_width)
    val deleteActionSizePx = with(density) { deleteActionSize.toPx() }
    val draggableState =
        remember {
            AnchoredDraggableState(
                initialValue = DragAnchors.Center,
                anchors =
                    DraggableAnchors {
                        DragAnchors.Center at 0f
                        if (onDelete != null) {
                            DragAnchors.End at deleteActionSizePx
                        }
                    },
                positionalThreshold = { distance: Float -> distance * 0.5f },
                velocityThreshold = { with(density) { 100.dp.toPx() } },
                animationSpec = tween(),
            )
        }

    val iconSize = dimensionResource(id = R.dimen.network_status_icon_size)
    val borderRadius = dimensionResource(id = R.dimen.common_panel_border_radius)
    val clipShape =
        remember {
            RoundedCornerShape(borderRadius)
        }
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(82.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier =
                Modifier
                    .offset {
                        IntOffset(
                            x =
                                if (onDelete != null) {
                                    -draggableState
                                        .requireOffset()
                                        .roundToInt()
                                } else {
                                    0
                                },
                            y = 0,
                        )
                    }
                    .anchoredDraggable(draggableState, Orientation.Horizontal, reverseDirection = true)
                    .noRippleClickable {
                        onClick(network, validator)
                    }
                    .background(
                        color = Color.panelBg(isDark),
                        shape = clipShape,
                    )
                    .padding(dimensionResource(id = R.dimen.common_padding))
                    .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.network_status_icon_spacing)),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (displayNetworkIcon) {
                    Image(
                        modifier = Modifier.size(iconSize),
                        painter =
                            painterResource(
                                when (network.id) {
                                    1L -> R.drawable.kusama_icon
                                    else -> R.drawable.polkadot_icon
                                },
                            ),
                        contentDescription = network.display,
                    )
                }
                if (validator.parentDisplay?.isNotEmpty() == true) {
                    if (validator.confirmed) {
                        Image(
                            modifier = Modifier.size(iconSize),
                            painter = painterResource(R.drawable.parent_identity_confirmed_icon),
                            contentDescription = "",
                        )
                    } else {
                        Image(
                            modifier = Modifier.size(iconSize),
                            painter = painterResource(R.drawable.parent_identity_not_confirmed_icon),
                            contentDescription = "",
                        )
                    }
                } else if (validator.display?.isNotEmpty() == true) {
                    if (validator.confirmed) {
                        Image(
                            modifier = Modifier.size(iconSize),
                            painter = painterResource(R.drawable.identity_confirmed_icon),
                            contentDescription = "",
                        )
                    } else {
                        Image(
                            modifier = Modifier.size(iconSize),
                            painter = painterResource(R.drawable.identity_not_confirmed_icon),
                            contentDescription = "",
                        )
                    }
                }
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    modifier =
                        Modifier
                            .weight(1.0f, fill = false)
                            .fillMaxWidth(),
                    text = validator.identityDisplay(),
                    style = Font.semiBold(16.sp),
                    color = Color.text(isDark),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (validator.isEnrolledIn1KV) {
                    Image(
                        modifier = Modifier.requiredSize(iconSize),
                        painter = painterResource(R.drawable.onekv_icon),
                        contentDescription = "",
                    )
                }
                if (validator.isParaValidator) {
                    Image(
                        modifier = Modifier.requiredSize(iconSize),
                        painter = painterResource(R.drawable.para_validator_icon),
                        contentDescription = "",
                    )
                }
                if (validator.isActiveNextSession) {
                    Image(
                        modifier = Modifier.requiredSize(iconSize),
                        painter = painterResource(R.drawable.active_next_session_icon),
                        contentDescription = "",
                    )
                }
                if (validator.heartbeatReceived == true) {
                    Image(
                        modifier = Modifier.requiredSize(iconSize),
                        painter = painterResource(R.drawable.heartbeat_received_icon),
                        contentDescription = "",
                    )
                }
                if (validator.oversubscribed) {
                    Image(
                        modifier = Modifier.requiredSize(iconSize),
                        painter = painterResource(R.drawable.oversubscribed_icon),
                        contentDescription = "",
                    )
                }
                if (validator.preferences.blocksNominations) {
                    Image(
                        modifier = Modifier.requiredSize(iconSize),
                        painter = painterResource(R.drawable.blocks_nominations_icon),
                        contentDescription = "",
                    )
                }
                if (validator.slashCount > 0) {
                    Image(
                        modifier = Modifier.requiredSize(iconSize),
                        painter = painterResource(R.drawable.slashed_icon),
                        contentDescription = "",
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = nominationsDisplay(network, validator),
                    style = Font.light(11.sp),
                    color = Color.text(isDark),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.weight(1.0f))
                if (displayActiveStatus) {
                    if (validator.isActive) {
                        Text(
                            text = stringResource(id = R.string.validator_list_active),
                            style = Font.light(12.sp),
                            color = Color.green(),
                        )
                    } else {
                        Text(
                            text = stringResource(id = R.string.validator_list_active),
                            style = Font.light(12.sp),
                            color = Color.red(),
                        )
                    }
                }
            }
        }
        val ratio = draggableState.requireOffset() / deleteActionSizePx
        Box(
            modifier =
                Modifier
                    .alpha(ratio)
                    .width(deleteActionSize * ratio)
                    .fillMaxHeight()
                    .align(Alignment.CenterEnd),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                modifier =
                    Modifier.noRippleClickable {
                        onDelete?.invoke(validator)
                    }.size(dimensionResource(id = R.dimen.my_validators_delete_button_size)),
                painter = painterResource(id = R.drawable.trash_icon_red),
                contentDescription = "",
                contentScale = ContentScale.None,
            )
        }
    }
}

@ThemePreviews
@Composable
fun ValidatorSummaryViewPreview(isDark: Boolean = isSystemInDarkTheme()) {
    Surface(
        color = Color.bg(isDark),
    ) {
        ValidatorSummaryView(
            modifier = Modifier,
            network = PreviewData.networks[0],
            displayNetworkIcon = true,
            displayActiveStatus = true,
            validator = PreviewData.validatorSummary,
            onClick = { _, _ -> },
        )
    }
}
