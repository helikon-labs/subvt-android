package io.helikon.subvt.ui.activity

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import io.helikon.subvt.data.repository.UserPreferencesRepository
import io.helikon.subvt.ui.navigation.AppNavigationHost
import io.helikon.subvt.ui.navigation.NavigationItem
import io.helikon.subvt.ui.theme.SubVTTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        super.onCreate(savedInstanceState)
        val userPreferencesRepository = UserPreferencesRepository(application)
        var startDestination = NavigationItem.Introduction.route
        runBlocking {
            if (userPreferencesRepository.userCreated.first()) {
                startDestination = NavigationItem.Onboarding.route
            }
        }
        setContent {
            // create a navhost controller
            val navController = rememberNavController()
            SubVTTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                ) {
                    AppNavigationHost(navController, startDestination)
                }
            }
        }
    }
}
