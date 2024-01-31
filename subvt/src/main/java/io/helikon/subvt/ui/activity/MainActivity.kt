package io.helikon.subvt.ui.activity

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import io.helikon.subvt.data.repository.UserPreferencesRepository
import io.helikon.subvt.ui.navigation.AppNavigationHost
import io.helikon.subvt.ui.navigation.NavigationItem
import io.helikon.subvt.ui.style.Color
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import android.graphics.Color as ComposeColor

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val systemBarStyle =
            when (
                val currentNightMode =
                    resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            ) {
                Configuration.UI_MODE_NIGHT_NO ->
                    SystemBarStyle.light(
                        ComposeColor.TRANSPARENT,
                        ComposeColor.TRANSPARENT,
                    )

                Configuration.UI_MODE_NIGHT_YES -> SystemBarStyle.dark(ComposeColor.TRANSPARENT)
                else -> error("Illegal State, current mode is $currentNightMode.")
            }
        enableEdgeToEdge(
            statusBarStyle = systemBarStyle,
            navigationBarStyle = systemBarStyle,
        )
        val userPreferencesRepository = UserPreferencesRepository(application)
        val startDestination =
            runBlocking {
                if (userPreferencesRepository.selectedNetworkId.first() > 0) {
                    NavigationItem.Main.route
                } else if (userPreferencesRepository.onboardingCompleted.first()) {
                    NavigationItem.NetworkSelection.route
                } else if (userPreferencesRepository.userCreated.first()) {
                    NavigationItem.Onboarding.route
                } else {
                    NavigationItem.Introduction.route
                }
            }
        setContent {
            MainActivityContent(startDestination = startDestination)
        }
    }
}

@Composable
fun MainActivityContent(
    isDark: Boolean = isSystemInDarkTheme(),
    startDestination: String,
) {
    // create a navhost controller
    val navController = rememberNavController()
    Surface(
        color = Color.bg(isDark),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize(),
        ) {
            AppNavigationHost(navController, startDestination)
        }
    }
}
