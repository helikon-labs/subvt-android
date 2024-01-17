package io.helikon.subvt.ui.screen.network.selection

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.helikon.subvt.data.DataRequestState
import io.helikon.subvt.data.model.Network
import io.helikon.subvt.data.repository.AppServiceRepository
import io.helikon.subvt.data.repository.NetworkRepository
import io.helikon.subvt.data.repository.UserPreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class NetworkSelectionViewModel
    @Inject
    constructor(
        private val userPreferencesRepository: UserPreferencesRepository,
        private val appServiceRepository: AppServiceRepository,
        private val networkRepository: NetworkRepository,
    ) : ViewModel() {
        var getNetworksState by mutableStateOf<DataRequestState<List<Network>>>(DataRequestState.Idle)
            private set
        val networks = networkRepository.allNetworks

        fun getNetworks() {
            val networks =
                networks.value.let {
                    if (it == null) {
                        return
                    }
                    it
                }
            if (networks.isNotEmpty()) {
                getNetworksState = DataRequestState.Success(networks)
                return
            }
            getNetworksState = DataRequestState.Loading
            viewModelScope.launch(Dispatchers.IO) {
                val response =
                    try {
                        appServiceRepository.getNetworks()
                    } catch (error: Throwable) {
                        getNetworksState = DataRequestState.Error(error)
                        return@launch
                    }
                if (response.isSuccess) {
                    response.getOrNull().let {
                        getNetworksState =
                            if (it == null) {
                                DataRequestState.Error(response.exceptionOrNull())
                            } else {
                                networkRepository.addAll(it.map(Network::from))
                                DataRequestState.Success(networks)
                            }
                    }
                } else {
                    getNetworksState = DataRequestState.Error(response.exceptionOrNull())
                }
            }
        }

        fun selectNetwork(network: Network) {
            runBlocking(Dispatchers.IO) {
                userPreferencesRepository.setSelectedNetworkId(network.id)
            }
        }
    }
