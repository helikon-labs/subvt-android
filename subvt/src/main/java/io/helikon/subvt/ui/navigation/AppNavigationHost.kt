package io.helikon.subvt.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseOut
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
            EnterTransition.None
        },
        exitTransition = {
            ExitTransition.None
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
        composable(
            route = NavigationItem.Main.route,
            enterTransition = {
                fadeIn(
                    animationSpec =
                        tween(
                            100,
                            easing = EaseInOut,
                        ),
                ) +
                    slideIntoContainer(
                        animationSpec =
                            tween(
                                200,
                                easing = EaseInOut,
                            ),
                        towards = AnimatedContentTransitionScope.SlideDirection.End,
                    )
            },
            exitTransition = {
                fadeOut(
                    animationSpec =
                        tween(
                            100,
                            easing = EaseInOut,
                        ),
                ) +
                    slideOutOfContainer(
                        animationSpec =
                            tween(
                                200,
                                easing = EaseInOut,
                            ),
                        towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    ) { fullOffset ->
                        fullOffset / 2
                    }
            },
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
                    animationSpec =
                        tween(
                            200,
                            easing = EaseInOut,
                        ),
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    animationSpec =
                        tween(
                            200,
                            easing = EaseInOut,
                        ),
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
            popEnterTransition = {
                slideIntoContainer(
                    animationSpec =
                        tween(
                            300,
                            easing = EaseIn,
                        ),
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    animationSpec =
                        tween(
                            300,
                            easing = EaseOut,
                        ),
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
