package io.helikon.subvt.ui.screen.network.status.panel

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.helikon.subvt.R
import io.helikon.subvt.ui.style.Font
import io.helikon.subvt.ui.theme.Color
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
fun EraEpochPanel(
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme(),
    isEra: Boolean,
    index: Int?,
    startTimestamp: Long?,
    endTimestamp: Long?,
) {
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
            textAlign = TextAlign.Center,
            style = Font.light(dimensionResource(id = R.dimen.network_status_panel_title_font_size).value.sp),
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
                text = "$elapsedPercentage%",
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
                var gradientWeight =
                    min(
                        elapsedPercentage,
                        5,
                    )
                gradientWeight =
                    min(
                        gradientWeight,
                        100 - elapsedPercentage,
                    )
                if ((elapsedPercentage - gradientWeight) > 0) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxHeight()
                                .weight((elapsedPercentage - gradientWeight).toFloat())
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
                if ((100 - elapsedPercentage - gradientWeight) > 0) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxHeight()
                                .weight((100 - elapsedPercentage - gradientWeight).toFloat())
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
        EraEpochPanel(
            modifier = Modifier,
            isEra = true,
            index = 1234,
            startTimestamp = Date().time - 3 * 60 * 60 * 1000,
            endTimestamp = Date().time + 1 * 60 * 60 * 1000 + 32 * 60 * 1000,
        )
    }
}
