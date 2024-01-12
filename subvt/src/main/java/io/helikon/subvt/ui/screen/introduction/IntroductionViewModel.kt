package io.helikon.subvt.ui.screen.introduction

import android.app.Application
import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.helikon.subvt.data.DataRequestState
import io.helikon.subvt.data.DataRequestState.Error
import io.helikon.subvt.data.DataRequestState.Idle
import io.helikon.subvt.data.DataRequestState.Loading
import io.helikon.subvt.data.DataRequestState.Success
import io.helikon.subvt.data.SubVTData
import io.helikon.subvt.data.model.app.User
import io.helikon.subvt.data.repository.UserPreferencesRepository
import io.helikon.subvt.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class IntroductionViewModel(application: Application) : AndroidViewModel(application) {
    private val userRepository = UserRepository(application)
    private val _createUserState = mutableStateOf<DataRequestState<User>>(Idle)
    private var userPreferencesRepository = UserPreferencesRepository(application)
    val createUserState: State<DataRequestState<User>>
        get() = _createUserState

    fun createUser(context: Context) {
        _createUserState.value = Loading
        viewModelScope.launch(Dispatchers.IO) {
            val response =
                try {
                    SubVTData.reset(context)
                    userRepository.createUser()
                } catch (error: Throwable) {
                    _createUserState.value = Error(error)
                    return@launch
                }
            userRepository.createUser()
            if (response.isSuccess) {
                response.getOrNull().let {
                    if (it == null) {
                        _createUserState.value = Error(response.exceptionOrNull())
                    } else {
                        userPreferencesRepository.setUserIsCreated(true)
                        _createUserState.value = Success(it)
                    }
                }
            } else {
                _createUserState.value = Error(response.exceptionOrNull())
            }
        }
    }
}
