package io.helikon.subvt.ui.screen.validator.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.helikon.subvt.data.preview.PreviewData
import io.helikon.subvt.data.repository.NetworkRepository
import io.helikon.subvt.data.repository.UserPreferencesRepository
import io.helikon.subvt.data.repository.ValidatorListRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ValidatorListViewModel
    @Inject
    constructor(
        private val userPreferencesRepository: UserPreferencesRepository,
        private val networkRepository: NetworkRepository,
        private val validatorListRepository: ValidatorListRepository,
    ) : ViewModel() {
        val serviceStatus = validatorListRepository.serviceStatus
        var selectedNetwork by mutableStateOf(PreviewData.networks[0])
            private set

        fun subscribe() {
            viewModelScope.launch(Dispatchers.IO) {
                val networkId = userPreferencesRepository.selectedNetworkId.first()
                if (networkId < 1) {
                    return@launch
                }
                selectedNetwork = networkRepository.findById(networkId)!!
                validatorListRepository.subscribe(selectedNetwork)
            }
        }

        fun unsubscribe() {
        }
    }
