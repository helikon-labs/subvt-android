package io.helikon.subvt.data.repository

import io.helikon.subvt.data.model.app.NetworkStatus
import io.helikon.subvt.data.model.app.NetworkStatusDiff
import io.helikon.subvt.data.service.NetworkStatusService
import io.helikon.subvt.data.service.RPCSubscriptionListener
import io.helikon.subvt.data.service.RPCSubscriptionService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NetworkStatusRepository : RPCSubscriptionListener<NetworkStatus, NetworkStatusDiff> {
    private var subscriptionId = -1L
    private lateinit var service: NetworkStatusService
    private val _networkStatus = MutableStateFlow<NetworkStatus?>(null)
    val networkStatus: StateFlow<NetworkStatus?> = _networkStatus

    suspend fun subscribe(
        host: String,
        port: Int,
    ) {
        this.service = NetworkStatusService(host, port, this)
        this.service.subscribe(listOf())
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
        // TODO update connection status
    }

    override suspend fun onUnsubscribed(
        service: RPCSubscriptionService<NetworkStatus, NetworkStatusDiff>,
        subscriptionId: Long,
    ) {
        if (this.subscriptionId != subscriptionId) {
            return
        }
        this.subscriptionId = -1L
        this._networkStatus.value = null
        // TODO update connection status
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
