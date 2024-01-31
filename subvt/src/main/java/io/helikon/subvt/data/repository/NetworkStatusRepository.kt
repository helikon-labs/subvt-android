package io.helikon.subvt.data.repository

import io.helikon.subvt.data.model.Network
import io.helikon.subvt.data.model.app.NetworkStatus
import io.helikon.subvt.data.model.app.NetworkStatusDiff
import io.helikon.subvt.data.service.NetworkStatusService
import io.helikon.subvt.data.service.RPCSubscriptionListener
import io.helikon.subvt.data.service.RPCSubscriptionService
import io.helikon.subvt.data.service.RPCSubscriptionServiceStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NetworkStatusRepository : RPCSubscriptionListener<NetworkStatus, NetworkStatusDiff> {
    private var subscriptionId = -1L
    private val service = NetworkStatusService(this)
    private val _networkStatus = MutableStateFlow<NetworkStatus?>(null)
    val serviceStatus = service.status
    val networkStatus: StateFlow<NetworkStatus?> = _networkStatus
    var onSubscribed: ((NetworkStatus) -> Unit)? = null

    private var network: Network? = null
    private var nextNetwork: Network? = null

    suspend fun changeNetwork(network: Network) {
        if (service.status.value is RPCSubscriptionServiceStatus.Subscribed) {
            nextNetwork = network
            this.service.unsubscribe()
        } else {
            this.subscribe(network)
        }
    }

    suspend fun subscribe(network: Network) {
        network.networkStatusServiceHost?.let { host ->
            network.networkStatusServicePort?.let { port ->
                this.network = network
                this.service.subscribe(
                    host,
                    port,
                    listOf(),
                )
            }
        }
    }

    suspend fun unsubscribe() {
        this.service.unsubscribe()
    }

    override suspend fun onSubscribed(
        service: RPCSubscriptionService<NetworkStatus, NetworkStatusDiff>,
        subscriptionId: Long,
        bestBlockNumber: Long?,
        finalizedBlockNumber: Long?,
        data: NetworkStatus,
    ) {
        this.subscriptionId = subscriptionId
        this._networkStatus.value = data
        this.onSubscribed?.invoke(data)
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
        this.nextNetwork?.let { network ->
            this.nextNetwork = null
            this.subscribe(network)
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
            this._networkStatus.value = this._networkStatus.value?.apply(diff)
        }
    }
}
