package io.helikon.subvt.ui.screen.validator.details.view

import android.graphics.Typeface
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.helikon.subvt.R
import io.helikon.subvt.data.model.Network
import io.helikon.subvt.ui.modifier.noRippleClickable
import io.helikon.subvt.ui.style.Color
import io.helikon.subvt.ui.style.Font
import io.helikon.subvt.util.formatDecimal
import io.helikon.subvt.util.truncateAddress
import java.math.BigInteger

@Composable
fun NominatorListView(
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme(),
    titleResourceId: Int,
    network: Network?,
    count: Int?,
    total: BigInteger?,
    nominations: List<Triple<String, Boolean, BigInteger>>?,
) {
    var isOpen by rememberSaveable {
        mutableStateOf(false)
    }
    val arrowDrawable =
        if (isOpen) {
            R.drawable.arrow_up
        } else {
            R.drawable.arrow_down
        }
    Column(
        modifier =
            modifier
                .noRippleClickable {
                    if (!nominations.isNullOrEmpty()) {
                        isOpen = !isOpen
                    }
                }
                .background(
                    color = Color.panelBg(isDark),
                    shape = RoundedCornerShape(dimensionResource(id = R.dimen.common_panel_border_radius)),
                )
                .padding(dimensionResource(id = R.dimen.common_padding))
                .fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text =
                    stringResource(id = titleResourceId) +
                        if (count != null) {
                            " ($count)"
                        } else {
                            ""
                        },
                style = Font.light(12.sp),
                color = Color.text(isDark),
            )
            if (!nominations.isNullOrEmpty()) {
                Image(painter = painterResource(id = arrowDrawable), contentDescription = "")
            }
        }
        Spacer(
            modifier = Modifier.height(dimensionResource(id = R.dimen.common_data_panel_content_margin_top)),
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.common_panel_padding)),
        ) {
            Text(
                text =
                    if (total != null && network != null) {
                        formatDecimal(
                            total,
                            network.tokenDecimalCount,
                        )
                    } else {
                        "-"
                    },
                style = Font.semiBold(28.sp),
                color = Color.text(isDark),
            )
            if (network != null) {
                Text(
                    text = network.tokenTicker,
                    style = Font.normal(28.sp),
                    color = Color.text(isDark).copy(alpha = 0.6f),
                )
            }
        }
        if (isOpen) {
            val monospaceFont = Font.normal(12.sp).copy(fontFamily = FontFamily(Typeface.MONOSPACE))
            Spacer(
                modifier = Modifier.height(dimensionResource(id = R.dimen.common_panel_padding)),
            )
            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .heightIn(
                            0.dp,
                            dimensionResource(id = R.dimen.validator_details_nomination_list_max_height),
                        ),
            ) {
                items(nominations ?: listOf(), key = { it.first }) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text =
                                truncateAddress(it.first) +
                                    if (it.second) {
                                        " (1KV)"
                                    } else {
                                        ""
                                    },
                            style = monospaceFont,
                            color = Color.text(isDark = isDark),
                        )
                        Spacer(modifier = Modifier.weight(1.0f))
                        Text(
                            text =
                                formatDecimal(
                                    it.third,
                                    network?.tokenDecimalCount ?: 0,
                                ),
                            style = monospaceFont,
                            color = Color.text(isDark = isDark),
                        )
                    }
                }
            }
        }
    }
}
