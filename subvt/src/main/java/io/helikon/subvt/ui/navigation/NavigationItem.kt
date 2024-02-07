package io.helikon.subvt.ui.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavType
import androidx.navigation.navArgument
import io.helikon.subvt.data.model.substrate.AccountId

sealed class NavigationItem {
    object Introduction {
        const val GENERIC_ROUTE = "introduction"
    }

    object Onboarding {
        const val GENERIC_ROUTE = "onboarding"
    }

    object NetworkSelection {
        const val GENERIC_ROUTE = "network_selection"
    }

    object Main {
        const val GENERIC_ROUTE = "main"
    }

    data class ValidatorList(val isActive: Boolean) {
        companion object {
            const val GENERIC_ROUTE =
                "validator_list/is_active/{is_active}"
            val arguments =
                listOf(
                    navArgument("is_active") { type = NavType.BoolType },
                )

            fun getIsActive(savedStateHandle: SavedStateHandle): Boolean = checkNotNull(savedStateHandle["is_active"])
        }

        val route = "validator_list/is_active/$isActive"
    }

    data class ValidatorDetails(
        val networkId: Long,
        val accountId: AccountId,
    ) {
        companion object {
            const val GENERIC_ROUTE =
                "validator_details/network/{network_id}/validator/{account_id_hex}"
            val arguments =
                listOf(
                    navArgument("network_id") { type = NavType.LongType },
                    navArgument("account_id_hex") { type = NavType.StringType },
                )

            fun getNetworkId(savedStateHandle: SavedStateHandle): Long = checkNotNull(savedStateHandle["network_id"])

            fun getAccountId(savedStateHandle: SavedStateHandle) = AccountId(checkNotNull<String>(savedStateHandle["account_id_hex"]))
        }

        val route = "validator_details/network/$networkId/validator/$accountId"
    }
}
