package io.helikon.subvt.ui.screen.validator.my

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
import io.helikon.subvt.data.extension.validatorIdentityComparator
import io.helikon.subvt.data.model.Network
import io.helikon.subvt.data.model.app.ValidatorSummary
import io.helikon.subvt.data.repository.NetworkRepository
import io.helikon.subvt.data.service.AppService
import io.helikon.subvt.data.service.ReportService
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyValidatorsViewModel
    @Inject
    constructor(
        @ApplicationContext context: Context,
        networkRepository: NetworkRepository,
    ) : ViewModel() {
        val networks = networkRepository.allNetworks
        private val appService =
            AppService(
                context,
                "https://${BuildConfig.API_HOST}:${BuildConfig.APP_SERVICE_PORT}/",
            )
        private lateinit var reportServices: List<Pair<Long, ReportService>>

        var validators by mutableStateOf<ImmutableList<ValidatorSummary>?>(null)
            private set
        var dataRequestState by mutableStateOf<DataRequestState<String>>(DataRequestState.Idle)
            private set

        fun initReportServices(networks: List<Network>) {
            if (!::reportServices.isInitialized) {
                reportServices =
                    networks.map { network ->
                        Pair(
                            network.id,
                            ReportService("https://${network.reportServiceHost!!}:${network.reportServicePort!!}"),
                        )
                    }
            }
        }

        fun getMyValidators() {
            if (dataRequestState == DataRequestState.Loading || !::reportServices.isInitialized) {
                return
            }
            dataRequestState = DataRequestState.Loading
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val result = appService.getUserValidators()
                    if (result.isSuccess) {
                        val validatorSummaries = mutableListOf<ValidatorSummary>()
                        val userValidators = result.getOrNull() ?: listOf()
                        for (userValidator in userValidators) {
                            val reportService =
                                reportServices.first { it.first == userValidator.networkId }.second
                            val validatorSummaryResult =
                                reportService.getValidatorSummary(userValidator.validatorAccountId.toString())
                            if (validatorSummaryResult.isSuccess) {
                                val validatorSummaryReport = validatorSummaryResult.getOrNull()
                                if (validatorSummaryReport == null) {
                                    dataRequestState = DataRequestState.Error(null)
                                    return@launch
                                } else {
                                    validatorSummaries.add(validatorSummaryReport.validatorSummary)
                                }
                            } else {
                                /*
                                dataRequestState =
                                    DataRequestState.Error(validatorSummaryResult.exceptionOrNull())
                                return@launch
                                 */
                            }
                        }
                        validatorSummaries.sortWith(validatorIdentityComparator)
                        validators = validatorSummaries.toImmutableList()
                        dataRequestState = DataRequestState.Success("")
                    } else {
                        if (validators?.isEmpty() == true) {
                            validators = null
                        }
                        dataRequestState = DataRequestState.Error(result.exceptionOrNull())
                    }
                } catch (e: Throwable) {
                    if (validators?.isEmpty() == true) {
                        validators = null
                    }
                    dataRequestState = DataRequestState.Error(e)
                }
            }
        }
    }
