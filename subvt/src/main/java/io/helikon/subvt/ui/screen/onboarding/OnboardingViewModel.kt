package io.helikon.subvt.ui.screen.onboarding

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.helikon.subvt.data.repository.UserPreferencesRepository
import io.helikon.subvt.data.repository.dataStore

class OnboardingViewModel(application: Application) : AndroidViewModel(application) {
    private var userPreferencesRepository = UserPreferencesRepository(application.dataStore)
}