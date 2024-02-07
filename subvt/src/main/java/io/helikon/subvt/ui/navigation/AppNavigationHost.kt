package io.helikon.subvt.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import io.helikon.subvt.ui.screen.introduction.IntroductionScreen
import io.helikon.subvt.ui.screen.main.MainScreen
import io.helikon.subvt.ui.screen.network.selection.NetworkSelectionScreen
import io.helikon.subvt.ui.screen.onboarding.OnboardingScreen
import io.helikon.subvt.ui.screen.validator.list.ValidatorListScreen

@Composable
fun AppNavigationHost(
    navController: NavHostController,
    startDestination: String,
) {
    NavHost(
        navController,
        startDestination,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None },
    ) {
        composable(route = NavigationItem.Introduction.route) {
            IntroductionScreen(onUserCreated = {
                navController.navigate(NavigationItem.Onboarding.route)
            })
        }
        composable(route = NavigationItem.Onboarding.route) {
            OnboardingScreen(onComplete = {
                navController.navigate((NavigationItem.NetworkSelection.route))
            })
        }
        composable(route = NavigationItem.NetworkSelection.route) {
            NetworkSelectionScreen(onComplete = {
                navController.navigate((NavigationItem.Main.route))
            })
        }
        composable(
            route = NavigationItem.Main.route,
            enterTransition = { null },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                )
            },
            popExitTransition = { null },
        ) {
            MainScreen(onActiveValidatorListButtonClicked = {
                navController.navigate((NavigationItem.ActiveValidatorList.route))
            }, onInactiveValidatorListButtonClicked = {
                navController.navigate((NavigationItem.InactiveValidatorList.route))
            })
        }
        composable(
            route = NavigationItem.ActiveValidatorList.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                )
            },
            popEnterTransition = { null },
            exitTransition = { null },
            popExitTransition = {
                fadeOut(targetAlpha = 1.0f) +
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.End,
                    )
            },
        ) {
            ValidatorListScreen(onBack = {
                navController.popBackStack()
            }, isActiveValidatorList = true)
        }
        composable(
            route = NavigationItem.InactiveValidatorList.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                )
            },
            popEnterTransition = { null },
            exitTransition = { null },
            popExitTransition = {
                fadeOut(targetAlpha = 1.0f) +
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.End,
                    )
            },
        ) {
            ValidatorListScreen(onBack = {
                navController.popBackStack()
            }, isActiveValidatorList = false)
        }
    }
}
