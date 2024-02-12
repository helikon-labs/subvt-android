package io.helikon.subvt.ui.screen.validator.details.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import io.helikon.subvt.R
import io.helikon.subvt.data.model.app.ValidatorDetails

@Composable
fun IconsView(
    modifier: Modifier = Modifier,
    validator: ValidatorDetails,
) {
    Column(modifier = modifier) {
        Row(
            modifier =
                Modifier
                    .wrapContentSize()
                    .align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.common_panel_padding)),
        ) {
            if (validator.onekvCandidateRecordId != null) {
                Image(
                    modifier = Modifier.size(dimensionResource(id = R.dimen.validator_details_icon_size)),
                    painter = painterResource(id = R.drawable.onekv_icon),
                    contentDescription = "",
                )
            }
            if (validator.isParaValidator) {
                Image(
                    modifier = Modifier.size(dimensionResource(id = R.dimen.validator_details_icon_size)),
                    painter = painterResource(id = R.drawable.para_validator_icon),
                    contentDescription = "",
                )
            }
            if (validator.isActiveNextSession) {
                Image(
                    modifier = Modifier.size(dimensionResource(id = R.dimen.validator_details_icon_size)),
                    painter = painterResource(id = R.drawable.active_next_session_icon),
                    contentDescription = "",
                )
            }
            if (validator.heartbeatReceived == true) {
                Image(
                    modifier = Modifier.size(dimensionResource(id = R.dimen.validator_details_icon_size)),
                    painter = painterResource(id = R.drawable.heartbeat_received_icon),
                    contentDescription = "",
                )
            }
            if (validator.oversubscribed) {
                Image(
                    modifier = Modifier.size(dimensionResource(id = R.dimen.validator_details_icon_size)),
                    painter = painterResource(id = R.drawable.oversubscribed_icon),
                    contentDescription = "",
                )
            }
            if (validator.preferences.blocksNominations) {
                Image(
                    modifier = Modifier.size(dimensionResource(id = R.dimen.validator_details_icon_size)),
                    painter = painterResource(id = R.drawable.blocks_nominations_icon),
                    contentDescription = "",
                )
            }
            if (validator.slashCount > 0) {
                Image(
                    modifier = Modifier.size(dimensionResource(id = R.dimen.validator_details_icon_size)),
                    painter = painterResource(id = R.drawable.slashed_icon),
                    contentDescription = "",
                )
            }
        }
    }
}
