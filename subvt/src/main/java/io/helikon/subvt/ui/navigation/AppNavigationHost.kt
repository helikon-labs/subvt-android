package io.helikon.subvt.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import io.helikon.subvt.ui.screen.introduction.IntroductionScreen
import io.helikon.subvt.ui.screen.onboarding.OnboardingScreen

@Composable
fun AppNavigationHost(
    navController: NavHostController,
    startDestination: String,
) {
    NavHost(
        navController,
        startDestination,
        enterTransition = {
            EnterTransition.None
        },
        exitTransition = {
            ExitTransition.None
        },
    ) {
        composable(NavigationItem.Introduction.route) {
            IntroductionScreen(onUserCreated = {
                navController.navigate(NavigationItem.Onboarding.route)
            })
        }
        composable(NavigationItem.Onboarding.route) {
            OnboardingScreen()
        }
    }
}
