package io.helikon.subvt.ui.navigation

enum class Screen {
    INTRODUCTION,
    ONBOARDING,
    NETWORK_SELECTION,
    MAIN,
}

sealed class NavigationItem(val route: String) {
    data object Introduction : NavigationItem(Screen.INTRODUCTION.name)

    data object Onboarding : NavigationItem(Screen.ONBOARDING.name)

    data object NetworkSelection : NavigationItem(Screen.NETWORK_SELECTION.name)

    data object Main : NavigationItem(Screen.MAIN.name)
}
