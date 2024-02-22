package io.helikon.subvt.ui.screen.validator.details.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.helikon.subvt.R
import io.helikon.subvt.data.model.app.ValidatorDetails
import io.helikon.subvt.ui.modifier.noRippleClickable
import io.helikon.subvt.ui.style.Color
import io.helikon.subvt.ui.style.Font

enum class Tooltip {
    ONEKV,
    PARAVALIDATOR,
    ACTIVE_NEXT_SESSION,
    HEARTBEAT_RECEIVED,
    OVERSUBSCRIBED,
    BLOCKS_NOMINATIONS,
    SLASHED,
}

fun Tooltip.textResourceId() =
    when (this) {
        Tooltip.ONEKV -> R.string.validator_details_tooltip_onekv
        Tooltip.PARAVALIDATOR -> R.string.validator_details_tooltip_paravalidator
        Tooltip.ACTIVE_NEXT_SESSION -> R.string.validator_details_tooltip_active_next_session
        Tooltip.HEARTBEAT_RECEIVED -> R.string.validator_details_tooltip_heartbeat_received
        Tooltip.OVERSUBSCRIBED -> R.string.validator_details_tooltip_oversubscribed
        Tooltip.BLOCKS_NOMINATIONS -> R.string.validator_details_tooltip_blocks_nominations
        Tooltip.SLASHED -> R.string.validator_details_tooltip_slashed
    }

@Composable
fun IconsView(
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme(),
    validator: ValidatorDetails,
) {
    var tooltip by remember { mutableStateOf<Tooltip?>(null) }
    var tooltipOffset by remember { mutableStateOf(0.dp) }
    var oneKVIconOffset by remember { mutableStateOf(0.dp) }
    var paravalidatorIconOffset by remember { mutableStateOf(0.dp) }
    var activeNextSessionIconOffset by remember { mutableStateOf(0.dp) }
    var heartbeatReceivedIconOffset by remember { mutableStateOf(0.dp) }
    var oversubscribedIconOffset by remember { mutableStateOf(0.dp) }
    var blocksNominationsIconOffset by remember { mutableStateOf(0.dp) }
    var slashedIconOffset by remember { mutableStateOf(0.dp) }
    var iconContainerWidth by remember { mutableFloatStateOf(0.0f) }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Column(
                modifier =
                    Modifier
                        .offset(tooltipOffset, 0.dp)
                        .wrapContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                tooltip?.let {
                    Text(
                        modifier =
                            Modifier
                                .background(
                                    color = Color.bg(isDark),
                                    shape = RoundedCornerShape(8.dp),
                                )
                                .padding(12.dp, 10.dp),
                        color = Color.text(isDark),
                        style = Font.light(11.sp),
                        text = stringResource(id = it.textResourceId()),
                    )
                    Canvas(modifier = Modifier.size(12.dp, 8.dp)) {
                        val path =
                            Path().apply {
                                moveTo(0.0f, 0.0f)
                                lineTo(size.width, 0.0f)
                                lineTo(size.width / 2.0f, size.height)
                                lineTo(0.0f, 0.0f)
                                close()
                            }
                        drawPath(path, Color.bg(isDark))
                    }
                }
            }
        }
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .onGloballyPositioned {
                        iconContainerWidth = it.boundsInParent().width
                    },
            horizontalArrangement = Arrangement.Center,
        ) {
            val localDensity = LocalDensity.current
            val margin = dimensionResource(id = R.dimen.validator_details_margin_horizontal)
            if (validator.onekvCandidateRecordId != null) {
                Spacer(modifier = Modifier.width(margin))
                Image(
                    modifier =
                        Modifier
                            .size(dimensionResource(id = R.dimen.validator_details_icon_size))
                            .noRippleClickable {
                                if (tooltip == Tooltip.ONEKV) {
                                    tooltip = null
                                    return@noRippleClickable
                                }
                                tooltip = Tooltip.ONEKV
                                tooltipOffset = oneKVIconOffset
                            }
                            .onGloballyPositioned {
                                oneKVIconOffset =
                                    with(localDensity) {
                                        (it.boundsInParent().center.x - (iconContainerWidth / 2.0f)).toDp()
                                    }
                            },
                    painter = painterResource(id = R.drawable.onekv_icon),
                    contentDescription = "",
                )
                Spacer(modifier = Modifier.width(margin))
            }
            if (validator.isParaValidator) {
                Spacer(modifier = Modifier.width(margin))
                Image(
                    modifier =
                        Modifier
                            .size(dimensionResource(id = R.dimen.validator_details_icon_size))
                            .noRippleClickable {
                                if (tooltip == Tooltip.PARAVALIDATOR) {
                                    tooltip = null
                                    return@noRippleClickable
                                }
                                tooltip = Tooltip.PARAVALIDATOR
                                tooltipOffset = paravalidatorIconOffset
                            }
                            .onGloballyPositioned {
                                paravalidatorIconOffset =
                                    with(localDensity) {
                                        (it.boundsInParent().center.x - (iconContainerWidth / 2.0f)).toDp()
                                    }
                            },
                    painter = painterResource(id = R.drawable.para_validator_icon),
                    contentDescription = "",
                )
                Spacer(modifier = Modifier.width(margin))
            }
            if (validator.isActiveNextSession) {
                Spacer(modifier = Modifier.width(margin))
                Image(
                    modifier =
                        Modifier
                            .size(dimensionResource(id = R.dimen.validator_details_icon_size))
                            .noRippleClickable {
                                if (tooltip == Tooltip.ACTIVE_NEXT_SESSION) {
                                    tooltip = null
                                    return@noRippleClickable
                                }
                                tooltip = Tooltip.ACTIVE_NEXT_SESSION
                                tooltipOffset = activeNextSessionIconOffset
                            }
                            .onGloballyPositioned {
                                activeNextSessionIconOffset =
                                    with(localDensity) {
                                        (it.boundsInParent().center.x - (iconContainerWidth / 2.0f)).toDp()
                                    }
                            },
                    painter = painterResource(id = R.drawable.active_next_session_icon),
                    contentDescription = "",
                )
                Spacer(modifier = Modifier.width(margin))
            }
            if (validator.heartbeatReceived == true) {
                Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.validator_details_margin_horizontal)))
                Image(
                    modifier =
                        Modifier
                            .size(dimensionResource(id = R.dimen.validator_details_icon_size))
                            .noRippleClickable {
                                if (tooltip == Tooltip.HEARTBEAT_RECEIVED) {
                                    tooltip = null
                                    return@noRippleClickable
                                }
                                tooltip = Tooltip.HEARTBEAT_RECEIVED
                                tooltipOffset = heartbeatReceivedIconOffset
                            }
                            .onGloballyPositioned {
                                heartbeatReceivedIconOffset =
                                    with(localDensity) {
                                        (it.boundsInParent().center.x - (iconContainerWidth / 2.0f)).toDp()
                                    }
                            },
                    painter = painterResource(id = R.drawable.heartbeat_received_icon),
                    contentDescription = "",
                )
                Spacer(modifier = Modifier.width(margin))
            }
            if (validator.oversubscribed) {
                Spacer(modifier = Modifier.width(margin))
                Image(
                    modifier =
                        Modifier
                            .size(dimensionResource(id = R.dimen.validator_details_icon_size))
                            .noRippleClickable {
                                if (tooltip == Tooltip.OVERSUBSCRIBED) {
                                    tooltip = null
                                    return@noRippleClickable
                                }
                                tooltip = Tooltip.OVERSUBSCRIBED
                                tooltipOffset = oversubscribedIconOffset
                            }
                            .onGloballyPositioned {
                                oversubscribedIconOffset =
                                    with(localDensity) {
                                        (it.boundsInParent().center.x - (iconContainerWidth / 2.0f)).toDp()
                                    }
                            },
                    painter = painterResource(id = R.drawable.oversubscribed_icon),
                    contentDescription = "",
                )
                Spacer(modifier = Modifier.width(margin))
            }
            if (validator.preferences.blocksNominations) {
                Spacer(modifier = Modifier.width(margin))
                Image(
                    modifier =
                        Modifier
                            .size(dimensionResource(id = R.dimen.validator_details_icon_size))
                            .noRippleClickable {
                                if (tooltip == Tooltip.BLOCKS_NOMINATIONS) {
                                    tooltip = null
                                    return@noRippleClickable
                                }
                                tooltip = Tooltip.BLOCKS_NOMINATIONS
                                tooltipOffset = blocksNominationsIconOffset
                            }
                            .onGloballyPositioned {
                                blocksNominationsIconOffset =
                                    with(localDensity) {
                                        (it.boundsInParent().center.x - (iconContainerWidth / 2.0f)).toDp()
                                    }
                            },
                    painter = painterResource(id = R.drawable.blocks_nominations_icon),
                    contentDescription = "",
                )
                Spacer(modifier = Modifier.width(margin))
            }
            if (validator.slashCount > 0) {
                Spacer(modifier = Modifier.width(margin))
                Image(
                    modifier =
                        Modifier
                            .size(dimensionResource(id = R.dimen.validator_details_icon_size))
                            .noRippleClickable {
                                if (tooltip == Tooltip.SLASHED) {
                                    tooltip = null
                                    return@noRippleClickable
                                }
                                tooltip = Tooltip.SLASHED
                                tooltipOffset = slashedIconOffset
                            }
                            .onGloballyPositioned {
                                slashedIconOffset =
                                    with(localDensity) {
                                        (it.boundsInParent().center.x - (iconContainerWidth / 2.0f)).toDp()
                                    }
                            },
                    painter = painterResource(id = R.drawable.slashed_icon),
                    contentDescription = "",
                )
                Spacer(modifier = Modifier.width(margin))
            }
        }
    }
}
