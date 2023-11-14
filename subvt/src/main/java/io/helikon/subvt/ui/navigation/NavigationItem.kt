package io.helikon.subvt.ui.navigation

enum class Screen {
    ONBOARDING, MAIN,
}

sealed class NavigationItem(val route: String) {
    object Onboarding : NavigationItem(Screen.ONBOARDING.name)
    object Main : NavigationItem(Screen.MAIN.name)
}