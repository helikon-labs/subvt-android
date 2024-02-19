package io.helikon.subvt.ui.screen.validator.details

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.helikon.subvt.BuildConfig
import io.helikon.subvt.R
import io.helikon.subvt.data.DataRequestState
import io.helikon.subvt.data.model.Network
import io.helikon.subvt.data.model.app.NewUserValidator
import io.helikon.subvt.data.model.app.UserValidator
import io.helikon.subvt.data.model.app.ValidatorDetails
import io.helikon.subvt.data.model.app.ValidatorDetailsDiff
import io.helikon.subvt.data.model.onekv.OneKVNominatorSummary
import io.helikon.subvt.data.repository.NetworkRepository
import io.helikon.subvt.data.service.AppService
import io.helikon.subvt.data.service.RPCSubscriptionListener
import io.helikon.subvt.data.service.RPCSubscriptionService
import io.helikon.subvt.data.service.ReportService
import io.helikon.subvt.data.service.ValidatorDetailsService
import io.helikon.subvt.ui.navigation.NavigationItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ValidatorDetailsViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        @ApplicationContext context: Context,
        private val networkRepository: NetworkRepository,
    ) : ViewModel(),
        RPCSubscriptionListener<ValidatorDetails, ValidatorDetailsDiff>,
        SensorEventListener {
        private val networkId = NavigationItem.ValidatorDetails.getNetworkId(savedStateHandle)
        val accountId = NavigationItem.ValidatorDetails.getAccountId(savedStateHandle)

        private val validatorDetailsService = ValidatorDetailsService(this)
        private var subscriptionId = -1L
        val validatorDetailsServiceStatus = validatorDetailsService.status

        private val appService =
            AppService(
                context,
                "https://${BuildConfig.API_HOST}:${BuildConfig.APP_SERVICE_PORT}/",
            )
        private var myValidators = mutableListOf<UserValidator>()
        private val _gotMyValidators = mutableStateOf(false)
        val gotMyValidators: State<Boolean> = _gotMyValidators
        private val _isMyValidator = mutableStateOf(false)
        val isMyValidator: State<Boolean> = _isMyValidator
        private val _appServiceStatus = mutableStateOf<DataRequestState<Nothing>>(DataRequestState.Idle)
        val appServiceStatus: State<DataRequestState<Nothing>> = _appServiceStatus

        private val feedbackDuration =
            context.resources.getInteger(R.integer.snackbar_short_display_duration_ms)
        private val _feedbackIsValidatorAdded = mutableStateOf<Boolean?>(null)
        val feedbackIsValidatorAdded: State<Boolean?> = _feedbackIsValidatorAdded

        private val sensorManager: SensorManager
        private val sensor: Sensor?
        private val rotation =
            floatArrayOf(
                1.0f, 0.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f, 0.0f,
            )
        private val orientation = floatArrayOf(0.0f, 0.0f, 0.0f)

        var network by mutableStateOf<Network?>(null)
            private set
        var validator by mutableStateOf<ValidatorDetails?>(null)
            private set
        var oneKVNominators by mutableStateOf<List<OneKVNominatorSummary>>(listOf())
            private set

        init {
            sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
            if (sensor == null) {
                Timber.e("Rotation vector sensor not found.")
            }
        }

        fun startSensor() {
            sensorManager.registerListener(this, sensor, 10_000)
        }

        fun stopSensor() {
            sensorManager.unregisterListener(this, sensor)
        }

        override fun onAccuracyChanged(
            sensor: Sensor?,
            accuracy: Int,
        ) {
            // no-op
        }

        override fun onSensorChanged(event: SensorEvent?) {
            if (event?.sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
                SensorManager.getRotationMatrixFromVector(
                    rotation,
                    event.values,
                )
                SensorManager.getOrientation(
                    rotation,
                    orientation,
                )
                val yaw = ((orientation[0] * 180 / Math.PI) + 90).toInt()
                // yaw += reset[0]
                val pitch = ((-orientation[2] * 180 / Math.PI) - 90).toInt()
                // pitch += reset[1]
                val roll = (-orientation[1] * 180 / Math.PI).toInt()
                // roll += reset[2]
                // Timber.d("($yaw,$pitch,$roll)")
            }
        }

        fun subscribe() {
            viewModelScope.launch(Dispatchers.IO) {
                networkRepository.findById(networkId)?.let { network ->
                    this@ValidatorDetailsViewModel.network = network
                    // get onekv nominators
                    getOneKVNominators()
                    network.validatorDetailsServiceHost?.let { host ->
                        network.validatorDetailsServicePort?.let { port ->
                            validatorDetailsService.subscribe(
                                host,
                                port,
                                listOf(accountId.toString()),
                            )
                        }
                    }
                }
            }
        }

        private suspend fun getOneKVNominators() {
            this.network?.reportServiceHost?.let { host ->
                this.network?.reportServicePort?.let { port ->
                    val reportService = ReportService("https://$host:$port")
                    val result = reportService.getOneKVNominatorSummaries()
                    result.getOrNull()?.let {
                        this.oneKVNominators = it
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
                validatorDetailsService.unsubscribe()
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
            update?.let {
                validator = validator?.apply(it)
            }
        }

        fun getMyValidators() {
            myValidators.clear()
            _appServiceStatus.value = DataRequestState.Loading
            viewModelScope.launch(Dispatchers.IO) {
                val result = appService.getUserValidators()
                if (result.isSuccess) {
                    myValidators.addAll(result.getOrNull() ?: listOf())
                    _isMyValidator.value = myValidators.count { it.validatorAccountId == accountId } > 0
                    _appServiceStatus.value = DataRequestState.Idle
                    _gotMyValidators.value = true
                } else {
                    _appServiceStatus.value = DataRequestState.Error(result.exceptionOrNull())
                }
            }
        }

        fun addValidator() {
            _appServiceStatus.value = DataRequestState.Loading
            viewModelScope.launch(Dispatchers.IO) {
                val request = NewUserValidator(networkId, accountId)
                val result = appService.createUserValidator(request)
                if (result.isSuccess) {
                    val userValidator = result.getOrNull()
                    if (userValidator != null) {
                        myValidators.add(userValidator)
                        _isMyValidator.value = true
                        _appServiceStatus.value = DataRequestState.Idle
                        _feedbackIsValidatorAdded.value = true
                        delay(feedbackDuration.toLong())
                        _feedbackIsValidatorAdded.value = null
                    } else {
                        _appServiceStatus.value = DataRequestState.Error(result.exceptionOrNull())
                    }
                } else {
                    _appServiceStatus.value = DataRequestState.Error(result.exceptionOrNull())
                }
            }
        }

        fun removeValidator() {
            _appServiceStatus.value = DataRequestState.Loading
            viewModelScope.launch(Dispatchers.IO) {
                myValidators.firstOrNull { it.validatorAccountId == accountId }?.let {
                    val result = appService.deleteUserValidator(it.id)
                    if (result.isSuccess) {
                        myValidators.removeAll { myValidator -> myValidator.validatorAccountId == accountId }
                        _isMyValidator.value = false
                        _appServiceStatus.value = DataRequestState.Idle
                        _feedbackIsValidatorAdded.value = false
                        delay(feedbackDuration.toLong())
                        _feedbackIsValidatorAdded.value = null
                    } else {
                        _appServiceStatus.value = DataRequestState.Error(result.exceptionOrNull())
                    }
                }
            }
        }
    }
