package io.helikon.subvt.ui.screen.network.status.panel

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.helikon.subvt.R
import io.helikon.subvt.data.model.Network
import io.helikon.subvt.data.preview.PreviewData
import io.helikon.subvt.ui.modifier.noRippleClickable
import io.helikon.subvt.ui.style.Color
import io.helikon.subvt.ui.style.Font
import io.helikon.subvt.ui.util.ThemePreviews

@Composable
fun NetworkSelectorButton(
    isDark: Boolean = isSystemInDarkTheme(),
    network: Network,
    isOpen: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .clip(shape = RoundedCornerShape(12.dp))
                .background(
                    if (isOpen) {
                        Color.networkSelectorOpenBg(isDark)
                    } else {
                        Color.networkSelectorClosedBg(isDark)
                    },
                )
                .noRippleClickable {
                    onClick()
                }
                .padding(12.dp, 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier.size(18.dp),
            painter =
                painterResource(
                    when (network.id) {
                        1L -> R.drawable.kusama_icon
                        else -> R.drawable.polkadot_icon
                    },
                ),
            contentDescription = "",
        )
        Text(
            modifier = Modifier.offset(0.dp, (-1).dp),
            text = network.display,
            color = Color.text(isDark),
            style = Font.normal(12.sp),
        )
        Image(
            modifier =
                Modifier
                    .size(18.dp)
                    .rotate(
                        if (isOpen) {
                            180f
                        } else {
                            0f
                        },
                    ),
            painter =
                painterResource(R.drawable.chevron_down_small),
            contentDescription = "",
        )
    }
}

@ThemePreviews
@Composable
fun NetworkSelectorButtonPreview(isDark: Boolean = isSystemInDarkTheme()) {
    Surface(
        color = Color.bg(isDark),
    ) {
        NetworkSelectorButton(
            network = PreviewData.networks[0],
            isOpen = false,
            onClick = {},
        )
    }
}
