package io.helikon.subvt.ui.screen.validator.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.helikon.subvt.data.extension.filter
import io.helikon.subvt.data.extension.validatorSummaryComparator
import io.helikon.subvt.data.model.app.ValidatorListUpdate
import io.helikon.subvt.data.model.app.ValidatorSummary
import io.helikon.subvt.data.model.substrate.AccountId
import io.helikon.subvt.data.preview.PreviewData
import io.helikon.subvt.data.repository.NetworkRepository
import io.helikon.subvt.data.repository.UserPreferencesRepository
import io.helikon.subvt.data.service.RPCSubscriptionListener
import io.helikon.subvt.data.service.RPCSubscriptionService
import io.helikon.subvt.data.service.ValidatorListService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject

@HiltViewModel
class ValidatorListViewModel
    @Inject
    constructor(
        private val userPreferencesRepository: UserPreferencesRepository,
        private val networkRepository: NetworkRepository,
    ) : ViewModel(), RPCSubscriptionListener<ValidatorListUpdate, ValidatorListUpdate> {
        private val service = ValidatorListService(this)
        private var subscriptionId = -1L
        val serviceStatus = service.status
        var selectedNetwork by mutableStateOf(PreviewData.networks[0])
            private set

        private val mutex = Mutex()
        private val _validators = mutableListOf<ValidatorSummary>()
        var validators by mutableStateOf<List<ValidatorSummary>>(listOf())
        var filter by mutableStateOf(TextFieldValue(""))

        fun subscribe(isActive: Boolean) {
            viewModelScope.launch(Dispatchers.IO) {
                val networkId = userPreferencesRepository.selectedNetworkId.first()
                if (networkId < 1) {
                    return@launch
                }
                selectedNetwork = networkRepository.findById(networkId)!!
                if (isActive) {
                    selectedNetwork.activeValidatorListServiceHost?.let { host ->
                        selectedNetwork.activeValidatorListServicePort?.let { port ->
                            service.subscribe(
                                host,
                                port,
                                listOf(),
                            )
                        }
                    }
                } else {
                    selectedNetwork.inactiveValidatorListServiceHost?.let { host ->
                        selectedNetwork.inactiveValidatorListServicePort?.let { port ->
                            service.subscribe(
                                host,
                                port,
                                listOf(),
                            )
                        }
                    }
                }
            }
        }

        fun unsubscribe() {
            viewModelScope.launch(Dispatchers.IO) {
                service.unsubscribe()
            }
        }

        override suspend fun onSubscribed(
            service: RPCSubscriptionService<ValidatorListUpdate, ValidatorListUpdate>,
            subscriptionId: Long,
            bestBlockNumber: Long?,
            finalizedBlockNumber: Long?,
            data: ValidatorListUpdate,
        ) {
            this.subscriptionId = subscriptionId
            _validators.clear()
            _validators.addAll(data.insert)
            sortAndFilter()
        }

        override suspend fun onUnsubscribed(
            service: RPCSubscriptionService<ValidatorListUpdate, ValidatorListUpdate>,
            subscriptionId: Long,
        ) {
            if (this.subscriptionId != subscriptionId) {
                return
            }
            this.subscriptionId = -1L
        }

        override suspend fun onUpdateReceived(
            service: RPCSubscriptionService<ValidatorListUpdate, ValidatorListUpdate>,
            subscriptionId: Long,
            bestBlockNumber: Long?,
            finalizedBlockNumber: Long?,
            update: ValidatorListUpdate?,
        ) {
            update?.let {
                mutex.lock()
                for (removeAccountIdHex in it.removeIds) {
                    val accountId = AccountId(hex = removeAccountIdHex)
                    _validators.removeAll { validator ->
                        validator.accountId == accountId
                    }
                }
                for (validatorDiff in it.update) {
                    val index =
                        _validators.indexOfFirst { validator ->
                            validator.accountId == validatorDiff.accountId
                        }
                    if (index >= 0) {
                        val validator = _validators.removeAt(index)
                        val updatedValidator = validator.apply(validatorDiff)
                        _validators.add(updatedValidator)
                    }
                }
                for (newValidator in it.insert) {
                    val index =
                        _validators.indexOfFirst { validator ->
                            validator.accountId == newValidator.accountId
                        }
                    if (index < 0) {
                        _validators.add(newValidator)
                    }
                }
                mutex.unlock()
                sortAndFilter()
            }
        }

        fun sortAndFilter() {
            viewModelScope.launch(Dispatchers.IO) {
                mutex.lock()
                _validators.sortWith(validatorSummaryComparator)
                mutex.unlock()
                validators =
                    _validators
                        .filter {
                            it.filter(filter.text)
                        }
                        .sortedWith(validatorSummaryComparator)
            }
        }
    }
