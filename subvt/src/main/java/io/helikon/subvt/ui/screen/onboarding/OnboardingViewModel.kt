package io.helikon.subvt.ui.screen.onboarding

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.helikon.subvt.data.repository.UserPreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class OnboardingViewModel(application: Application) : AndroidViewModel(application) {
    private var userPreferencesRepository = UserPreferencesRepository(application)

    fun completeOnboarding() {
        runBlocking(Dispatchers.IO) {
            userPreferencesRepository.setOnboardingCompleted(true)
        }
    }
}
