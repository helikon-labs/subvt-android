package io.helikon.subvt.ui.navigation

enum class Screen {
    INTRODUCTION,
    ONBOARDING,
    MAIN,
}

sealed class NavigationItem(val route: String) {
    object Introduction : NavigationItem(Screen.INTRODUCTION.name)
    object Onboarding : NavigationItem(Screen.ONBOARDING.name)
    object Main : NavigationItem(Screen.MAIN.name)
}