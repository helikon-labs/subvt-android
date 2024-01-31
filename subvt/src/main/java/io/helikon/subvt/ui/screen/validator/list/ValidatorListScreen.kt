package io.helikon.subvt.ui.screen.validator.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import io.helikon.subvt.ui.style.Color

data class ValidatorListScreenState(
    val dummy: Int = 0,
)

@Composable
fun ValidatorListScreen(
    modifier: Modifier = Modifier,
    viewModel: ValidatorListViewModel = hiltViewModel(),
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
) {
    DisposableEffect(lifecycleOwner) {
        // Create an observer that triggers our remembered callbacks
        // for sending analytics events
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_START) {
                    viewModel.subscribe()
                } else if (event == Lifecycle.Event.ON_STOP) {
                    viewModel.unsubscribe()
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    ValidatorListScreenContent(
        modifier = modifier,
        state = ValidatorListScreenState(dummy = 1),
    )
}

@Composable
fun ValidatorListScreenContent(
    modifier: Modifier = Modifier,
    state: ValidatorListScreenState,
) {
    Box(modifier = modifier.fillMaxSize().background(Color.blue())) {
    }
}
