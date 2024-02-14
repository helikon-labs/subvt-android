package io.helikon.subvt.ui.screen.network.selection

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
import io.helikon.subvt.data.model.Network
import io.helikon.subvt.data.repository.NetworkRepository
import io.helikon.subvt.data.repository.UserPreferencesRepository
import io.helikon.subvt.data.service.AppService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class NetworkSelectionViewModel
    @Inject
    constructor(
        @ApplicationContext context: Context,
        private val userPreferencesRepository: UserPreferencesRepository,
        private val networkRepository: NetworkRepository,
    ) : ViewModel() {
        var getNetworksState by mutableStateOf<DataRequestState<List<Network>>>(DataRequestState.Idle)
            private set
        val networks = networkRepository.allNetworks
        private val appService =
            AppService(
                context,
                "https://${BuildConfig.API_HOST}:${BuildConfig.APP_SERVICE_PORT}/",
            )

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
                        appService.getNetworks()
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
