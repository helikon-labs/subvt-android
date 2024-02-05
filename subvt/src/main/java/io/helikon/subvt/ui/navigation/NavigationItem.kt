package io.helikon.subvt.ui.navigation

enum class Screen {
    INTRODUCTION,
    ONBOARDING,
    NETWORK_SELECTION,
    MAIN,
    ACTIVE_VALIDATOR_LIST,
    INACTIVE_VALIDATOR_LIST,
}

sealed class NavigationItem(val route: String) {
    data object Introduction : NavigationItem(Screen.INTRODUCTION.name)

    data object Onboarding : NavigationItem(Screen.ONBOARDING.name)

    data object NetworkSelection : NavigationItem(Screen.NETWORK_SELECTION.name)

    data object Main : NavigationItem(Screen.MAIN.name)

    data object ActiveValidatorList : NavigationItem(Screen.ACTIVE_VALIDATOR_LIST.name)

    data object InactiveValidatorList : NavigationItem(Screen.INACTIVE_VALIDATOR_LIST.name)
}
