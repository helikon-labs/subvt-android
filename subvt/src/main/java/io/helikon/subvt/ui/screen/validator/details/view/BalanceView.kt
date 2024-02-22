package io.helikon.subvt.ui.screen.validator.details.view

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import io.helikon.subvt.R
import io.helikon.subvt.data.model.Network
import io.helikon.subvt.ui.style.Color
import io.helikon.subvt.ui.style.Font
import io.helikon.subvt.util.formatDecimal
import java.math.BigInteger

@Composable
fun BalanceView(
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme(),
    titleResourceId: Int,
    network: Network?,
    balance: BigInteger?,
) {
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
            text = stringResource(id = titleResourceId),
            style = Font.light(dimensionResource(id = R.dimen.common_panel_title_font_size).value.sp),
            color = Color.text(isDark),
        )
        Spacer(
            modifier = Modifier.height(dimensionResource(id = R.dimen.common_data_panel_content_margin_top)),
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.common_panel_padding)),
        ) {
            Text(
                text =
                    if (balance != null && network != null) {
                        formatDecimal(
                            balance,
                            network.tokenDecimalCount,
                        )
                    } else {
                        "-"
                    },
                style = Font.semiBold(26.sp),
                color = Color.text(isDark),
            )
            if (network != null) {
                Text(
                    text = network.tokenTicker,
                    style = Font.normal(26.sp),
                    color = Color.text(isDark).copy(alpha = 0.6f),
                )
            }
        }
    }
}
