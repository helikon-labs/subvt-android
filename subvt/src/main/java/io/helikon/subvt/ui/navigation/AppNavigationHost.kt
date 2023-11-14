package io.helikon.subvt.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import io.helikon.subvt.ui.screen.onboarding.OnboardingScreen

@Composable
fun AppNavigationHost(
    navController: NavHostController,
    startDestination: String = NavigationItem.Onboarding.route,
) {
    NavHost(
        navController,
        startDestination,
    ) {
        composable(NavigationItem.Onboarding.route) {
            OnboardingScreen()
        }
    }
}