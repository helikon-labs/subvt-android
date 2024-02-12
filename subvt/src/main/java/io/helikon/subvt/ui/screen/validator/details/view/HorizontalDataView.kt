package io.helikon.subvt.ui.screen.validator.details.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import io.helikon.subvt.R
import io.helikon.subvt.ui.style.Color
import io.helikon.subvt.ui.style.Font

@Composable
fun HorizontalDataView(
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme(),
    titleResourceId: Int,
    text: String,
    color: androidx.compose.ui.graphics.Color = Color.text(isDark = isSystemInDarkTheme()),
    displayExclamation: Boolean = false,
) {
    Row(
        modifier =
            modifier
                .background(
                    color = Color.panelBg(isDark),
                    shape = RoundedCornerShape(dimensionResource(id = R.dimen.common_panel_border_radius)),
                )
                .padding(dimensionResource(id = R.dimen.common_padding))
                .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(id = titleResourceId),
            style = Font.light(12.sp),
            color = Color.text(isDark),
        )
        Spacer(modifier = Modifier.weight(1.0f))
        Text(
            text = text,
            style = Font.semiBold(20.sp),
            color = color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (displayExclamation) {
            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.common_panel_padding)))
            Image(
                painter = painterResource(id = R.drawable.blue_exclamation),
                contentDescription = "",
            )
        }
    }
}
