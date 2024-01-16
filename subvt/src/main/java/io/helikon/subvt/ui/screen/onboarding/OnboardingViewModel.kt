package io.helikon.subvt.ui.screen.onboarding

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.helikon.subvt.data.repository.UserPreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel
    @Inject
    constructor(private val userPreferencesRepository: UserPreferencesRepository) :
    ViewModel() {
        fun completeOnboarding() {
            runBlocking(Dispatchers.IO) {
                userPreferencesRepository.setOnboardingCompleted(true)
            }
        }
    }
