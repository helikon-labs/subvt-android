package io.helikon.subvt.data.extension

import android.content.Context
import io.helikon.subvt.R
import io.helikon.subvt.data.model.substrate.RewardDestination
import io.helikon.subvt.data.model.substrate.RewardDestinationType
import io.helikon.subvt.util.truncateAddress

fun RewardDestination.display(
    context: Context,
    prefix: Short,
): String {
    return when (this.destinationType) {
        RewardDestinationType.ACCOUNT ->
            if (this.destination != null) {
                truncateAddress(this.destination!!.getAddress(prefix))
            } else {
                "-"
            }

        RewardDestinationType.CONTROLLER -> context.resources.getString(R.string.reward_destination_controller)
        RewardDestinationType.NONE -> context.resources.getString(R.string.reward_destination_none)
        RewardDestinationType.STAKED -> context.resources.getString(R.string.reward_destination_staked)
        RewardDestinationType.STASH -> context.resources.getString(R.string.reward_destination_stash)
    }
}
