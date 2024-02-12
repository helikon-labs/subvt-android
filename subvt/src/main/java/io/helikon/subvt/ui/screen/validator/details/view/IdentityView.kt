package io.helikon.subvt.ui.screen.validator.details.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.helikon.subvt.R
import io.helikon.subvt.data.extension.hasIdentity
import io.helikon.subvt.data.extension.hasParentIdentity
import io.helikon.subvt.data.extension.identityConfirmed
import io.helikon.subvt.data.extension.identityDisplay
import io.helikon.subvt.data.extension.parentIdentityConfirmed
import io.helikon.subvt.data.model.app.ValidatorDetails
import io.helikon.subvt.ui.style.Color
import io.helikon.subvt.ui.style.Font

@Composable
fun IdentityView(
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme(),
    validator: ValidatorDetails?,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val identityIcon =
            when {
                validator?.parentIdentityConfirmed() == true -> R.drawable.parent_identity_confirmed_icon
                validator?.identityConfirmed() == true -> R.drawable.identity_confirmed_icon
                validator?.hasParentIdentity() == true -> R.drawable.parent_identity_not_confirmed_icon
                validator?.hasIdentity() == true -> R.drawable.identity_not_confirmed_icon
                else -> null
            }
        identityIcon?.let {
            Image(
                modifier = Modifier.requiredSize(24.dp),
                painter = painterResource(id = it),
                contentDescription = "",
            )
        }
        Spacer(modifier = Modifier.requiredWidth(dimensionResource(id = R.dimen.common_panel_padding)))
        Text(
            textAlign = TextAlign.Center,
            text = validator?.identityDisplay() ?: "-",
            style = Font.normal(24.sp),
            color = Color.text(isDark),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
