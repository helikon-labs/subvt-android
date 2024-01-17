package io.helikon.subvt.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import io.helikon.subvt.ui.screen.introduction.IntroductionScreen
import io.helikon.subvt.ui.screen.main.MainScreen
import io.helikon.subvt.ui.screen.network.selection.NetworkSelectionScreen
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
            fadeIn()
        },
        exitTransition = {
            fadeOut()
        },
    ) {
        composable(NavigationItem.Introduction.route) {
            IntroductionScreen(onUserCreated = {
                navController.navigate(NavigationItem.Onboarding.route)
            })
        }
        composable(NavigationItem.Onboarding.route) {
            OnboardingScreen(onComplete = {
                navController.navigate((NavigationItem.NetworkSelection.route))
            })
        }
        composable(NavigationItem.NetworkSelection.route) {
            NetworkSelectionScreen(onComplete = {
                navController.navigate((NavigationItem.Main.route))
            })
        }
        composable(NavigationItem.Main.route) {
            MainScreen()
        }
    }
}
