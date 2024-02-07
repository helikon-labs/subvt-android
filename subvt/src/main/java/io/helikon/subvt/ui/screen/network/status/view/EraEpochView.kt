package io.helikon.subvt.ui.screen.network.status.view

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.helikon.subvt.R
import io.helikon.subvt.ui.style.Color
import io.helikon.subvt.ui.style.Font
import io.helikon.subvt.ui.util.ThemePreviews
import io.helikon.subvt.util.Quadruple
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.min

private const val SINGLE_QUOTE_ESCAPE = "{sq}"
private val DATE_FORMATTER = SimpleDateFormat("dd MMM '${SINGLE_QUOTE_ESCAPE}'yy", Locale.US)
private val TIME_FORMATTER = SimpleDateFormat("HH:mm", Locale.US)

@Composable
fun EraEpochView(
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme(),
    isEra: Boolean,
    index: Int?,
    startTimestamp: Long?,
    endTimestamp: Long?,
) {
    var isLaunched by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(index) {
        isLaunched = index != null
    }
    val progressVisibleRatio by animateFloatAsState(
        targetValue =
            if (isLaunched) {
                1.0f
            } else {
                0.0f
            },
        animationSpec =
            tween(durationMillis = 500, delayMillis = 750, easing = LinearEasing),
        label = "",
    )
    val title =
        stringResource(
            id =
                if (isEra) {
                    R.string.network_status_era_title
                } else {
                    R.string.network_status_epoch_title
                },
        )
    val (titleIndexed, dateText, elapsedPercentage, remainingTimeText) =
        if (index != null && startTimestamp != null && endTimestamp != null) {
            val startDate = Date(startTimestamp)
            val endDate = Date(endTimestamp)
            val now = Date()
            val remainingMs = endTimestamp - now.time
            val remainingHours = remainingMs / 1000 / 60 / 60
            val remainingMinutes = (remainingMs - (remainingHours * 60 * 60 * 1000)) / 1000 / 60
            val dateText =
                DATE_FORMATTER.format(startDate).replace(
                    SINGLE_QUOTE_ESCAPE,
                    "'",
                ) +
                    " / " + TIME_FORMATTER.format(startDate) +
                    " - " + TIME_FORMATTER.format(endDate)
            val elapsedPercentage =
                min(
                    100,
                    (now.time - startTimestamp) * 100 / (endTimestamp - startTimestamp),
                )
            val remainingTimeText =
                if (remainingHours > 0) {
                    String.format(
                        stringResource(R.string.network_status_remaining_hours),
                        remainingHours,
                    ) + " "
                } else {
                    ""
                } +
                    if (remainingMinutes == 1L) {
                        String.format(
                            stringResource(R.string.network_status_remaining_minute),
                            remainingMinutes,
                        )
                    } else {
                        String.format(
                            stringResource(R.string.network_status_remaining_minutes),
                            remainingMinutes,
                        )
                    }
            Quadruple(
                "$title $index",
                dateText,
                elapsedPercentage,
                remainingTimeText,
            )
        } else {
            Quadruple(title, "-", 0L, "-")
        }
    Column(
        modifier
            .clip(shape = RoundedCornerShape(dimensionResource(id = R.dimen.common_panel_border_radius)))
            .background(Color.panelBg(isDark))
            .padding(dimensionResource(id = R.dimen.common_padding)),
    ) {
        Text(
            text = titleIndexed,
            color = Color.text(isDark),
            textAlign = TextAlign.Center,
            style = Font.light(dimensionResource(id = R.dimen.network_status_panel_title_font_size).value.sp),
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = dateText,
            color = Color.text(isDark),
            textAlign = TextAlign.Start,
            style = Font.light(11.sp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.common_padding)))
        Row(
            verticalAlignment = Alignment.Bottom,
        ) {
            Text(
                textAlign = TextAlign.Start,
                modifier =
                    Modifier
                        .width(62.dp)
                        .alignByBaseline(),
                text = "${(elapsedPercentage * progressVisibleRatio).toInt()}%",
                style = Font.semiBold(20.sp),
                color = Color.text(isDark),
            )
            Row(
                modifier =
                    Modifier
                        .offset(0.dp, (-4).dp)
                        .height(6.dp)
                        .weight(1.0f)
                        .background(Color.statusActive()),
            ) {
                val animatedElapsedPercentage = (elapsedPercentage * progressVisibleRatio).toLong()
                var gradientWeight =
                    min(
                        animatedElapsedPercentage,
                        5,
                    )
                gradientWeight =
                    min(
                        gradientWeight,
                        100 - animatedElapsedPercentage,
                    )
                if ((animatedElapsedPercentage - gradientWeight) > 0) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxHeight()
                                .weight((animatedElapsedPercentage - gradientWeight).toFloat())
                                .background(
                                    Color.progress(),
                                ),
                    )
                }
                if (gradientWeight > 0) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxHeight()
                                .weight(gradientWeight * 2f)
                                .background(
                                    brush =
                                        Brush.horizontalGradient(
                                            colors = listOf(Color.progress(), Color.statusActive()),
                                        ),
                                ),
                    )
                }
                if ((100 - animatedElapsedPercentage - gradientWeight) > 0) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxHeight()
                                .weight((100 - animatedElapsedPercentage - gradientWeight).toFloat())
                                .background(
                                    Color.statusActive(),
                                ),
                    )
                }
            }
        }
        Row(
            verticalAlignment = Alignment.Top,
        ) {
            Spacer(modifier = Modifier.width(62.dp))
            Text(
                text = remainingTimeText,
                maxLines = 1,
                textAlign = TextAlign.Start,
                color = Color.remainingTime(isDark),
                style = Font.light(10.sp),
            )
        }
    }
}

@ThemePreviews
@Composable
fun EraEpochPanelPreview(isDark: Boolean = isSystemInDarkTheme()) {
    Surface(
        color = Color.bg(isDark),
    ) {
        EraEpochView(
            modifier = Modifier,
            isEra = true,
            index = 1234,
            startTimestamp = Date().time - 3 * 60 * 60 * 1000,
            endTimestamp = Date().time + 1 * 60 * 60 * 1000 + 32 * 60 * 1000,
        )
    }
}
