package io.helikon.subvt.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import io.helikon.subvt.R
import io.helikon.subvt.data.service.RPCSubscriptionServiceStatus
import io.helikon.subvt.ui.style.Color

@Composable
fun ServiceStatusIndicator(
    modifier: Modifier = Modifier,
    serviceStatus: RPCSubscriptionServiceStatus,
) {
    Box(
        modifier =
            modifier
                .size(dimensionResource(id = R.dimen.status_indicator_circle_size))
                .clip(CircleShape)
                .background(
                    when (serviceStatus) {
                        is RPCSubscriptionServiceStatus.Idle -> Color.statusIdle()
                        is RPCSubscriptionServiceStatus.Connected -> Color.statusWaiting()
                        is RPCSubscriptionServiceStatus.Connecting -> Color.statusWaiting()
                        is RPCSubscriptionServiceStatus.Error -> Color.statusError()
                        is RPCSubscriptionServiceStatus.Subscribed -> Color.statusActive()
                        is RPCSubscriptionServiceStatus.Unsubscribed -> Color.statusWaiting()
                    },
                ),
    )
}
