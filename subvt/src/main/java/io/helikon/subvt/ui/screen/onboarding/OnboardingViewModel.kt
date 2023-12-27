package io.helikon.subvt.ui.screen.onboarding

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.helikon.subvt.data.repository.UserPreferencesRepository
import io.helikon.subvt.data.repository.dataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class OnboardingViewModel(application: Application) : AndroidViewModel(application) {

}