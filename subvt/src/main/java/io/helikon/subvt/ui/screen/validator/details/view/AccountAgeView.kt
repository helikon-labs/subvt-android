package io.helikon.subvt.ui.screen.validator.details.view

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import io.helikon.subvt.R
import io.helikon.subvt.ui.style.Color
import io.helikon.subvt.ui.style.Font
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId

@Composable
fun AccountAgeView(
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme(),
    discoveredAt: Long,
) {
    val period =
        Period.between(
            Instant.ofEpochMilli(discoveredAt).atZone(ZoneId.systemDefault()).toLocalDate(),
            LocalDate.now(),
        )
    val periodText =
        if (period.years > 0) {
            String.format(
                stringResource(R.string.validator_details_period_years),
                period.years,
            )
        } else {
            ""
        } +
            if (period.months > 0) {
                " " +
                    String.format(
                        stringResource(R.string.validator_details_period_months),
                        period.months,
                    )
            } else {
                ""
            } +
            if (period.days > 0) {
                val days = period.days % 7
                val weeks = period.days / 7
                var weeksText = ""
                if (weeks > 0) {
                    weeksText =
                        " " +
                        String.format(
                            stringResource(R.string.validator_details_period_weeks),
                            weeks,
                        )
                }
                if (days > 0) {
                    "$weeksText " +
                        String.format(
                            stringResource(R.string.validator_details_period_days),
                            days,
                        )
                } else {
                    weeksText
                }
            } else {
                ""
            }
    Column(
        modifier =
            modifier
                .background(
                    color = Color.panelBg(isDark),
                    shape = RoundedCornerShape(dimensionResource(id = R.dimen.common_panel_border_radius)),
                )
                .padding(dimensionResource(id = R.dimen.common_padding))
                .fillMaxWidth(),
    ) {
        Text(
            text = stringResource(id = R.string.validator_details_account_age),
            style = Font.light(12.sp),
            color = Color.text(isDark),
        )
        Spacer(
            modifier = Modifier.height(dimensionResource(id = R.dimen.common_data_panel_content_margin_top)),
        )
        Text(
            text = periodText.trim(),
            style = Font.semiBold(20.sp),
            color = Color.text(isDark),
        )
    }
}
