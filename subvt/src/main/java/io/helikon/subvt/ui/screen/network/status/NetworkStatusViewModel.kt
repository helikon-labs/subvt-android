package io.helikon.subvt.ui.screen.network.status

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.helikon.subvt.data.repository.NetworkRepository
import io.helikon.subvt.data.repository.NetworkStatusRepository
import io.helikon.subvt.data.repository.UserPreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NetworkStatusViewModel
    @Inject
    constructor(
        private val userPreferencesRepository: UserPreferencesRepository,
        private val networkRepository: NetworkRepository,
        private val networkStatusRepository: NetworkStatusRepository,
    ) : ViewModel() {
        val networkStatus = networkStatusRepository.networkStatus

        fun subscribe() {
            viewModelScope.launch(Dispatchers.IO) {
                val networkId = userPreferencesRepository.selectedNetworkId.first()
                if (networkId < 1) {
                    return@launch
                }
                val network = networkRepository.findById(networkId)
                network?.networkStatusServiceHost?.let { host ->
                    network.networkStatusServicePort?.let { port ->
                        networkStatusRepository.subscribe(host, port)
                    }
                }
            }
        }

        fun unsubscribe() {
            viewModelScope.launch(Dispatchers.IO) {
                networkStatusRepository.unsubscribe()
            }
        }
    }
