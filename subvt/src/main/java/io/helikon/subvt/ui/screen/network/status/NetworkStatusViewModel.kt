package io.helikon.subvt.ui.screen.network.status

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.helikon.subvt.data.model.Network
import io.helikon.subvt.data.preview.PreviewData
import io.helikon.subvt.data.repository.NetworkRepository
import io.helikon.subvt.data.repository.NetworkStatusRepository
import io.helikon.subvt.data.repository.UserPreferencesRepository
import io.helikon.subvt.data.service.ReportService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

const val ERA_REPORT_COUNT = 15

@HiltViewModel
class NetworkStatusViewModel
    @Inject
    constructor(
        private val userPreferencesRepository: UserPreferencesRepository,
        private val networkRepository: NetworkRepository,
        private val networkStatusRepository: NetworkStatusRepository,
    ) : ViewModel() {
        val networkStatus = networkStatusRepository.networkStatus
        val serviceStatus = networkStatusRepository.serviceStatus
        var selectedNetwork by mutableStateOf(PreviewData.networks[0])
            private set
        val networks = networkRepository.allNetworks

        var activeValidatorCountList by mutableStateOf(listOf<Int>())
            private set
        var inactiveValidatorCountList by mutableStateOf(listOf<Int>())
            private set

        init {
            this.networkStatusRepository.onSubscribed = { status ->
                this.getEraValidatorCounts(
                    status.activeEra.index - ERA_REPORT_COUNT,
                    status.activeEra.index,
                )
            }
        }

        fun subscribe() {
            viewModelScope.launch(Dispatchers.IO) {
                val networkId = userPreferencesRepository.selectedNetworkId.first()
                if (networkId < 1) {
                    return@launch
                }
                selectedNetwork = networkRepository.findById(networkId)!!
                networkStatusRepository.subscribe(selectedNetwork)
            }
        }

        fun unsubscribe() {
            viewModelScope.launch(Dispatchers.IO) {
                networkStatusRepository.unsubscribe()
            }
        }

        fun changeNetwork(network: Network) {
            selectedNetwork = network
            viewModelScope.launch(Dispatchers.IO) {
                userPreferencesRepository.setSelectedNetworkId(network.id)
                viewModelScope.launch(Dispatchers.IO) {
                    networkStatusRepository.changeNetwork(network)
                }
            }
        }

        private fun getEraValidatorCounts(
            startEraIndex: Int,
            endEraIndex: Int,
        ) {
            selectedNetwork.reportServiceHost?.let { host ->
                selectedNetwork.reportServicePort?.let { port ->
                    viewModelScope.launch(Dispatchers.IO) {
                        Timber.i("CH $host $port")
                        val reportService =
                            ReportService(
                                "https://$host:$port/",
                            )
                        val result = reportService.getEraReport(startEraIndex, endEraIndex)
                        result.getOrNull()?.let { reports ->
                            activeValidatorCountList =
                                reports.map { report ->
                                    report.activeValidatorCount
                                }
                            inactiveValidatorCountList =
                                reports.map { report ->
                                    report.inactiveValidatorCount
                                }
                            Timber.i("ACTIVE :: $activeValidatorCountList")
                            Timber.i("INACTIVE :: $inactiveValidatorCountList")
                        }
                    }
                }
            }
        }
    }
