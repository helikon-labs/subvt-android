package io.helikon.subvt.ui.screen.onboarding

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.helikon.subvt.data.DataRequestState
import io.helikon.subvt.data.Error
import io.helikon.subvt.data.Idle
import io.helikon.subvt.data.Loading
import io.helikon.subvt.data.Success
import io.helikon.subvt.data.repository.UserPreferencesRepository
import io.helikon.subvt.data.repository.UserRepository
import io.helikon.subvt.data.repository.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class IntroductionViewModel(application: Application) : AndroidViewModel(application) {
    private val userRepository = UserRepository(application)
    private val _createUserState = mutableStateOf<DataRequestState>(Idle)
    private var userPreferencesRepository = UserPreferencesRepository(application.dataStore)
    val createUserState: State<DataRequestState>
        get() = _createUserState

    fun createUser() {
        _createUserState.value = Loading
        viewModelScope.launch(Dispatchers.IO) {
            val response = userRepository.createUser()
            if (response.isSuccess) {
                response.getOrNull().let {
                    if (it == null) {
                        _createUserState.value = Error(null)
                    } else {
                        userPreferencesRepository.setUserCreated(true)
                        _createUserState.value = Success(it)
                    }
                }
            } else {
                _createUserState.value = Error(response.exceptionOrNull())
            }
        }
    }
}