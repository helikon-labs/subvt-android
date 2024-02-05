package io.helikon.subvt.ui.screen.network.status

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.helikon.subvt.data.model.Network
import io.helikon.subvt.data.model.app.NetworkStatus
import io.helikon.subvt.data.model.app.NetworkStatusDiff
import io.helikon.subvt.data.preview.PreviewData
import io.helikon.subvt.data.repository.NetworkRepository
import io.helikon.subvt.data.repository.UserPreferencesRepository
import io.helikon.subvt.data.service.NetworkStatusService
import io.helikon.subvt.data.service.RPCSubscriptionListener
import io.helikon.subvt.data.service.RPCSubscriptionService
import io.helikon.subvt.data.service.RPCSubscriptionServiceStatus
import io.helikon.subvt.data.service.ReportService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

const val ERA_REPORT_COUNT = 15

@HiltViewModel
class NetworkStatusViewModel
    @Inject
    constructor(
        private val userPreferencesRepository: UserPreferencesRepository,
        private val networkRepository: NetworkRepository,
    ) : ViewModel(), RPCSubscriptionListener<NetworkStatus, NetworkStatusDiff> {
        private var subscriptionId = -1L
        private val service = NetworkStatusService(this)
        val serviceStatus = service.status
        var networkStatus by mutableStateOf<NetworkStatus?>(null)
            private set

        var selectedNetwork by mutableStateOf(PreviewData.networks[0])
            private set
        private var nextNetwork: Network? = null
        val networks = networkRepository.allNetworks

        val activeValidatorCountList = mutableStateListOf<Int>()
        val inactiveValidatorCountList = mutableStateListOf<Int>()

        fun subscribe() {
            viewModelScope.launch(Dispatchers.IO) {
                val networkId = userPreferencesRepository.selectedNetworkId.first()
                if (networkId < 1) {
                    return@launch
                }
                selectedNetwork = networkRepository.findById(networkId)!!
                selectedNetwork.networkStatusServiceHost?.let { host ->
                    selectedNetwork.networkStatusServicePort?.let { port ->
                        service.subscribe(
                            host,
                            port,
                            listOf(),
                        )
                    }
                }
            }
        }

        override suspend fun onSubscribed(
            service: RPCSubscriptionService<NetworkStatus, NetworkStatusDiff>,
            subscriptionId: Long,
            bestBlockNumber: Long?,
            finalizedBlockNumber: Long?,
            data: NetworkStatus,
        ) {
            this.subscriptionId = subscriptionId
            networkStatus = data
            getEraValidatorCounts(
                data.activeEra.index - ERA_REPORT_COUNT,
                data.activeEra.index,
            )
        }

        fun unsubscribe() {
            viewModelScope.launch(Dispatchers.IO) {
                service.unsubscribe()
            }
        }

        override suspend fun onUnsubscribed(
            service: RPCSubscriptionService<NetworkStatus, NetworkStatusDiff>,
            subscriptionId: Long,
        ) {
            if (this.subscriptionId != subscriptionId) {
                return
            }
            this.subscriptionId = -1L
            // this._networkStatus.value = null
            nextNetwork?.let {
                nextNetwork = null
                subscribe()
            }
        }

        override suspend fun onUpdateReceived(
            service: RPCSubscriptionService<NetworkStatus, NetworkStatusDiff>,
            subscriptionId: Long,
            bestBlockNumber: Long?,
            finalizedBlockNumber: Long?,
            update: NetworkStatusDiff?,
        ) {
            if (this.subscriptionId != subscriptionId) {
                return
            }
            update?.let { diff ->
                networkStatus = networkStatus?.apply(diff)
            }
        }

        fun changeNetwork(network: Network) {
            selectedNetwork = network
            activeValidatorCountList.clear()
            inactiveValidatorCountList.clear()
            viewModelScope.launch(Dispatchers.IO) {
                userPreferencesRepository.setSelectedNetworkId(network.id)
                viewModelScope.launch(Dispatchers.IO) {
                    if (service.status.value is RPCSubscriptionServiceStatus.Subscribed) {
                        nextNetwork = network
                        service.unsubscribe()
                    } else {
                        subscribe()
                    }
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
                        val reportService =
                            ReportService(
                                "https://$host:$port/",
                            )
                        val result = reportService.getEraReport(startEraIndex, endEraIndex)
                        result.getOrNull()?.let { reports ->
                            activeValidatorCountList.clear()
                            inactiveValidatorCountList.clear()
                            activeValidatorCountList.addAll(
                                reports.map { report ->
                                    report.activeValidatorCount
                                },
                            )
                            inactiveValidatorCountList.addAll(
                                reports.map { report ->
                                    report.inactiveValidatorCount
                                },
                            )
                        }
                    }
                }
            }
        }
    }
