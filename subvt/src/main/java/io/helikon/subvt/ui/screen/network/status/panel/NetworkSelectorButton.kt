package io.helikon.subvt.ui.screen.network.status.panel

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.helikon.subvt.R
import io.helikon.subvt.data.model.Network
import io.helikon.subvt.data.preview.PreviewData
import io.helikon.subvt.ui.modifier.noRippleClickable
import io.helikon.subvt.ui.theme.SubVTTheme
import io.helikon.subvt.ui.theme.lexendDecaFamily
import io.helikon.subvt.ui.util.ThemePreviews

@Composable
fun NetworkSelectorButton(network: Network) {
    Row(
        modifier =
            Modifier
                .clip(shape = RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(12.dp, 10.dp)
                .noRippleClickable {
                    // no-op
                },
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
            fontFamily = lexendDecaFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
        )
        Image(
            modifier = Modifier.size(18.dp),
            painter =
                painterResource(R.drawable.chevron_down_small),
            contentDescription = "",
        )
    }
}

@ThemePreviews
@Composable
fun NetworkSelectorButtonPreview() {
    SubVTTheme {
        Surface(
            color = MaterialTheme.colorScheme.surface,
        ) {
            NetworkSelectorButton(network = PreviewData.networks[0])
        }
    }
}
