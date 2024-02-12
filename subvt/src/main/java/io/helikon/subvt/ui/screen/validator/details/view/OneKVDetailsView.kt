package io.helikon.subvt.ui.screen.validator.details.view

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import io.helikon.subvt.R
import io.helikon.subvt.data.model.app.ValidatorDetails
import io.helikon.subvt.ui.style.Color
import io.helikon.subvt.ui.style.Font

@Composable
fun OneKVDetailsView(
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme(),
    validator: ValidatorDetails,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.common_panel_padding)),
    ) {
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.common_panel_padding)))
        Text(
            text = stringResource(id = R.string.validator_details_onekv),
            style = Font.semiBold(18.sp),
            color = Color.text(isDark),
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.common_panel_padding)))
        HorizontalDataView(
            titleResourceId = R.string.validator_details_onekv_rank,
            text = validator.onekvRank?.toString() ?: "-",
        )
        HorizontalDataView(
            titleResourceId = R.string.validator_details_onekv_location,
            text = validator.onekvLocation ?: "-",
        )
        HorizontalDataView(
            titleResourceId = R.string.validator_details_onekv_validity,
            text =
                stringResource(
                    id =
                        if (validator.onekvIsValid == true) {
                            R.string.validator_details_onekv_valid
                        } else {
                            R.string.validator_details_onekv_invalid
                        },
                ),
            color =
                if (validator.onekvIsValid == true) {
                    Color.text(isDark)
                } else {
                    Color.statusError()
                },
        )
    }
}
