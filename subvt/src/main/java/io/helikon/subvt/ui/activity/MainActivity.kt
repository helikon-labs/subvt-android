package io.helikon.subvt.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import io.helikon.subvt.data.repository.UserPreferencesRepository
import io.helikon.subvt.ui.navigation.AppNavigationHost
import io.helikon.subvt.ui.navigation.NavigationItem
import io.helikon.subvt.ui.style.Color
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import android.graphics.Color as ComposeColor

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted: Boolean ->
            if (isGranted) {
                getNotificationToken()
            }
        }

    private fun getNotificationToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(
            OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Timber.e("Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }
                val token = task.result
                Timber.i("Notification token received: $token")
            },
        )
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

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
                    NavigationItem.Main.GENERIC_ROUTE
                } else if (userPreferencesRepository.onboardingCompleted.first()) {
                    NavigationItem.NetworkSelection.GENERIC_ROUTE
                } else if (userPreferencesRepository.userCreated.first()) {
                    NavigationItem.Onboarding.GENERIC_ROUTE
                } else {
                    NavigationItem.Introduction.GENERIC_ROUTE
                }
            }
        setContent {
            MainActivityContent(startDestination = startDestination)
        }
        askNotificationPermission()
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
