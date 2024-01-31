package io.helikon.subvt.data.repository

import io.helikon.subvt.data.model.Network
import io.helikon.subvt.data.model.app.ValidatorListUpdate
import io.helikon.subvt.data.service.RPCSubscriptionListener
import io.helikon.subvt.data.service.RPCSubscriptionService
import io.helikon.subvt.data.service.ValidatorListService
import timber.log.Timber

class ValidatorListRepository : RPCSubscriptionListener<ValidatorListUpdate, ValidatorListUpdate> {
    private var subscriptionId = -1L
    private val service = ValidatorListService(this)
    val serviceStatus = service.status

    private var network: Network? = null

    suspend fun subscribe(network: Network) {
        network.activeValidatorListServiceHost?.let { host ->
            network.activeValidatorListServicePort?.let { port ->
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
        service: RPCSubscriptionService<ValidatorListUpdate, ValidatorListUpdate>,
        subscriptionId: Long,
        bestBlockNumber: Long?,
        finalizedBlockNumber: Long?,
        data: ValidatorListUpdate,
    ) {
        this.subscriptionId = subscriptionId
        Timber.i("Subscribed to validator list.")
    }

    override suspend fun onUnsubscribed(
        service: RPCSubscriptionService<ValidatorListUpdate, ValidatorListUpdate>,
        subscriptionId: Long,
    ) {
        if (this.subscriptionId != subscriptionId) {
            return
        }
        this.subscriptionId = -1L
        Timber.i("Unsubscribed from validator list.")
    }

    override suspend fun onUpdateReceived(
        service: RPCSubscriptionService<ValidatorListUpdate, ValidatorListUpdate>,
        subscriptionId: Long,
        bestBlockNumber: Long?,
        finalizedBlockNumber: Long?,
        update: ValidatorListUpdate?,
    ) {
        Timber.i("Validator list update received.")
    }
}
