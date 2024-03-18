package io.helikon.subvt.ui.screen.report.network

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.helikon.subvt.data.DataRequestState
import io.helikon.subvt.data.model.Network
import io.helikon.subvt.data.repository.NetworkRepository
import io.helikon.subvt.data.service.ReportService
import javax.inject.Inject

@HiltViewModel
class NetworkReportsViewModel
    @Inject
    constructor(
        @ApplicationContext context: Context,
        networkRepository: NetworkRepository,
    ) : ViewModel() {
        val networks = networkRepository.allNetworks
        private lateinit var reportServices: List<Pair<Long, ReportService>>
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
    }
