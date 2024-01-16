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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NetworkSelectionViewModel
    @Inject
    constructor(
        private val appServiceRepository: AppServiceRepository,
        private val repository: NetworkRepository,
    ) : ViewModel() {
        var getNetworksState by mutableStateOf<DataRequestState<List<Network>>>(DataRequestState.Loading)
            private set
        var selectedNetwork by mutableStateOf<Network?>(null)
            private set

        fun getNetworks() {
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
                                val networks = it.map(Network::from)
                                repository.addAll(networks)
                                DataRequestState.Success(networks)
                            }
                    }
                } else {
                    getNetworksState = DataRequestState.Error(response.exceptionOrNull())
                }
            }
        }

        fun selectNetwork(network: Network) {
            selectedNetwork = network
        }
    }
