package io.helikon.subvt.ui.screen.validator.details.view

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import io.helikon.subvt.R
import io.helikon.subvt.ui.style.Color
import io.helikon.subvt.ui.style.Font

@Composable
fun VerticalDataView(
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme(),
    titleResourceId: Int,
    text: String,
    color: androidx.compose.ui.graphics.Color = Color.text(isDark = isSystemInDarkTheme()),
) {
    Column(
        modifier =
            modifier
                .background(
                    color = Color.panelBg(isDark),
                    shape = RoundedCornerShape(dimensionResource(id = R.dimen.common_panel_border_radius)),
                )
                .padding(dimensionResource(id = R.dimen.common_padding)),
    ) {
        Text(
            text = stringResource(id = titleResourceId),
            style = Font.light(dimensionResource(id = R.dimen.common_panel_title_font_size).value.sp),
            color = Color.text(isDark),
        )
        Spacer(
            modifier = Modifier.height(dimensionResource(id = R.dimen.common_data_panel_content_margin_top)),
        )
        Text(
            text = text,
            style = Font.semiBold(18.sp),
            color = color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
