package io.helikon.subvt.ui.screen.introduction

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.helikon.subvt.BuildConfig
import io.helikon.subvt.data.DataRequestState
import io.helikon.subvt.data.DataRequestState.Error
import io.helikon.subvt.data.DataRequestState.Idle
import io.helikon.subvt.data.DataRequestState.Loading
import io.helikon.subvt.data.DataRequestState.Success
import io.helikon.subvt.data.SubVTData
import io.helikon.subvt.data.model.app.User
import io.helikon.subvt.data.repository.UserPreferencesRepository
import io.helikon.subvt.data.service.AppService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntroductionViewModel
    @Inject
    constructor(
        @ApplicationContext context: Context,
        private val userPreferencesRepository: UserPreferencesRepository,
    ) : ViewModel() {
        init {
            SubVTData.reset(context)
        }

        var createUserState by mutableStateOf<DataRequestState<User>>(Idle)
            private set
        private val appService =
            AppService(
                context,
                "https://${BuildConfig.API_HOST}:${BuildConfig.APP_SERVICE_PORT}/",
            )

        fun createUser() {
            createUserState = Loading
            viewModelScope.launch(Dispatchers.IO) {
                val response =
                    try {
                        appService.createUser()
                    } catch (error: Throwable) {
                        createUserState = Error(error)
                        return@launch
                    }
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
