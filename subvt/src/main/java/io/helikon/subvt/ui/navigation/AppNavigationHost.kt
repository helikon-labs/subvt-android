package io.helikon.subvt.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
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
import io.helikon.subvt.ui.screen.validator.list.ValidatorListScreen

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
        composable(route = NavigationItem.Main.route) {
            MainScreen(onValidatorListButtonClicked = {
                navController.navigate((NavigationItem.ValidatorList.route))
            })
        }
        composable(
            route = NavigationItem.ValidatorList.route,
            enterTransition = {
                fadeIn(
                    animationSpec =
                        tween(
                            300, easing = LinearEasing,
                        ),
                ) +
                    slideIntoContainer(
                        animationSpec = tween(300, easing = EaseIn),
                        towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    )
            },
            exitTransition = {
                fadeOut(
                    animationSpec =
                        tween(
                            300, easing = LinearEasing,
                        ),
                ) +
                    slideOutOfContainer(
                        animationSpec = tween(300, easing = EaseOut),
                        towards = AnimatedContentTransitionScope.SlideDirection.End,
                    )
            },
        ) {
            ValidatorListScreen()
        }
    }
}
