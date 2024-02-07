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
import io.helikon.subvt.ui.screen.validator.details.ValidatorDetailsScreen
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
        composable(route = NavigationItem.Introduction.GENERIC_ROUTE) {
            IntroductionScreen(onUserCreated = {
                navController.navigate(NavigationItem.Onboarding.GENERIC_ROUTE)
            })
        }
        composable(route = NavigationItem.Onboarding.GENERIC_ROUTE) {
            OnboardingScreen(onComplete = {
                navController.navigate((NavigationItem.NetworkSelection.GENERIC_ROUTE))
            })
        }
        composable(route = NavigationItem.NetworkSelection.GENERIC_ROUTE) {
            NetworkSelectionScreen(onComplete = {
                navController.navigate((NavigationItem.Main.GENERIC_ROUTE))
            })
        }
        composable(
            route = NavigationItem.Main.GENERIC_ROUTE,
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
                navController.navigate((NavigationItem.ValidatorList(true).route))
            }, onInactiveValidatorListButtonClicked = {
                navController.navigate((NavigationItem.ValidatorList(false).route))
            })
        }
        composable(
            route = NavigationItem.ValidatorList.GENERIC_ROUTE,
            arguments = NavigationItem.ValidatorList.arguments,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                )
            },
            exitTransition = { null },
            popExitTransition = {
                fadeOut(targetAlpha = 1.0f) +
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.End,
                    )
            },
        ) {
            ValidatorListScreen(
                onBack = {
                    navController.popBackStack()
                },
                onSelectValidator = { network, validator ->
                    navController.navigate(
                        NavigationItem.ValidatorDetails(
                            networkId = network.id,
                            accountId = validator.accountId,
                        ).route,
                    )
                },
            )
        }
        composable(
            route = NavigationItem.ValidatorDetails.GENERIC_ROUTE,
            arguments = NavigationItem.ValidatorDetails.arguments,
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
            ValidatorDetailsScreen(onBack = {
                navController.popBackStack()
            })
        }
    }
}
