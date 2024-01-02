package io.helikon.subvt.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import io.helikon.subvt.R
import io.helikon.subvt.ui.theme.Blue
import io.helikon.subvt.ui.theme.Light
import io.helikon.subvt.ui.theme.SubVTTheme
import io.helikon.subvt.ui.util.ThemePreviews

@Composable
fun ActionButton(
    text: String,
    isLoading: Boolean = false,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier
            .height(dimensionResource(id = R.dimen.action_button_height))
            .width(dimensionResource(id = R.dimen.action_button_width)),
        contentAlignment = Alignment.Center,
    ) {
        Button(
            onClick,
            enabled = !isLoading,
            modifier = Modifier
                .height(dimensionResource(id = R.dimen.action_button_height))
                .width(dimensionResource(id = R.dimen.action_button_width)),
            shape = RoundedCornerShape(dimensionResource(id = R.dimen.action_button_border_radius)),
            colors = ButtonDefaults.buttonColors(
                containerColor = Blue,
                contentColor = Light,
                disabledContainerColor = Blue,
                disabledContentColor = Color.Transparent,
            ),
        ) {
            Text(
                text = if (isLoading) "" else text,
                style = MaterialTheme.typography.labelMedium,
                color = Light,
                textAlign = TextAlign.Center,
            )
        }
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.width(dimensionResource(id = R.dimen.action_button_progress_width)),
                color = Light,
                trackColor = Color.Transparent,
            )
        }
    }
}

@ThemePreviews
@Composable
fun ActionButtonPreview() {
    SubVTTheme {
        ActionButton(text = "Action", false, Modifier) {
            // no-op
        }
    }
}