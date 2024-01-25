package io.helikon.subvt.ui.screen.network.status.panel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.helikon.subvt.R
import io.helikon.subvt.ui.theme.SubVTTheme
import io.helikon.subvt.ui.util.ThemePreviews

@Composable
fun BlockNumberPanel(
    modifier: Modifier = Modifier,
    title: String,
    blockNumber: Long?,
) {
    Box(
        modifier =
            modifier
                .height(128.dp)
                .clip(shape = RoundedCornerShape(dimensionResource(id = R.dimen.common_panel_border_radius)))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(dimensionResource(id = R.dimen.common_padding)),
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = dimensionResource(id = R.dimen.network_status_panel_title_font_size).value.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text =
                    if (blockNumber == null) {
                        "-"
                    } else {
                        "$blockNumber"
                    },
                style = MaterialTheme.typography.labelMedium,
                fontSize = 40.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@ThemePreviews
@Composable
fun BlockNumberPanelPreview() {
    SubVTTheme {
        Surface(
            color = MaterialTheme.colorScheme.surface,
        ) {
            BlockNumberPanel(
                modifier = Modifier,
                title = stringResource(id = R.string.network_status_best_block_number),
                blockNumber = 12_345_678,
            )
        }
    }
}