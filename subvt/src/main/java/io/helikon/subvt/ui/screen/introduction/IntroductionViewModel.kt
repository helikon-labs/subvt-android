package io.helikon.subvt.ui.screen.introduction

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
    var createUserState by mutableStateOf<DataRequestState<User>>(Idle)
        private set

    private var userPreferencesRepository = UserPreferencesRepository(application)

    fun createUser(context: Context) {
        createUserState = Loading
        viewModelScope.launch(Dispatchers.IO) {
            val response =
                try {
                    SubVTData.reset(context)
                    userRepository.createUser()
                } catch (error: Throwable) {
                    createUserState = Error(error)
                    return@launch
                }
            userRepository.createUser()
            if (response.isSuccess) {
                response.getOrNull().let {
                    createUserState =
                        if (it == null) {
                            Error(response.exceptionOrNull())
                        } else {
                            userPreferencesRepository.setUserIsCreated(true)
                            Success(it)
                        }
                }
            } else {
                createUserState = Error(response.exceptionOrNull())
            }
        }
    }
}
