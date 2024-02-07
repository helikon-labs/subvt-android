package io.helikon.subvt.ui.screen.validator.details

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.helikon.subvt.data.model.Network
import io.helikon.subvt.data.model.app.ValidatorDetails
import io.helikon.subvt.data.model.app.ValidatorDetailsDiff
import io.helikon.subvt.data.repository.NetworkRepository
import io.helikon.subvt.data.service.RPCSubscriptionListener
import io.helikon.subvt.data.service.RPCSubscriptionService
import io.helikon.subvt.data.service.ValidatorDetailsService
import io.helikon.subvt.ui.navigation.NavigationItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ValidatorDetailsViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        private val networkRepository: NetworkRepository,
    ) : ViewModel(), RPCSubscriptionListener<ValidatorDetails, ValidatorDetailsDiff> {
        private val service = ValidatorDetailsService(this)
        private var subscriptionId = -1L
        val serviceStatus = service.status
        private val networkId = NavigationItem.ValidatorDetails.getNetworkId(savedStateHandle)
        private val accountId = NavigationItem.ValidatorDetails.getAccountId(savedStateHandle)

        var network by mutableStateOf<Network?>(null)
            private set
        var validator by mutableStateOf<ValidatorDetails?>(null)
            private set

        fun subscribe() {
            viewModelScope.launch(Dispatchers.IO) {
                networkRepository.findById(networkId)?.let { network ->
                    this@ValidatorDetailsViewModel.network = network
                    network.validatorDetailsServiceHost?.let { host ->
                        network.validatorDetailsServicePort?.let { port ->
                            service.subscribe(
                                host,
                                port,
                                listOf(accountId.toString()),
                            )
                        }
                    }
                }
            }
        }

        override suspend fun onSubscribed(
            service: RPCSubscriptionService<ValidatorDetails, ValidatorDetailsDiff>,
            subscriptionId: Long,
            bestBlockNumber: Long?,
            finalizedBlockNumber: Long?,
            data: ValidatorDetails,
        ) {
            this.subscriptionId = subscriptionId
            this.validator = data
        }

        fun unsubscribe() {
            viewModelScope.launch(Dispatchers.IO) {
                service.unsubscribe()
            }
        }

        override suspend fun onUnsubscribed(
            service: RPCSubscriptionService<ValidatorDetails, ValidatorDetailsDiff>,
            subscriptionId: Long,
        ) {
            if (this.subscriptionId != subscriptionId) {
                return
            }
            this.subscriptionId = -1L
        }

        override suspend fun onUpdateReceived(
            service: RPCSubscriptionService<ValidatorDetails, ValidatorDetailsDiff>,
            subscriptionId: Long,
            bestBlockNumber: Long?,
            finalizedBlockNumber: Long?,
            update: ValidatorDetailsDiff?,
        ) {
            Timber.d("7")
            update?.let {
                validator = validator?.apply(it)
            }
        }
    }
