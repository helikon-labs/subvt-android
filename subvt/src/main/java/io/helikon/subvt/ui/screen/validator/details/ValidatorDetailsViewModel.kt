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
import dev.romainguy.kotlin.math.Float3
import io.helikon.subvt.BuildConfig
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
        private val _isMyValidator = mutableStateOf(false)
        val isMyValidator: State<Boolean> = _isMyValidator
        private val _myValidatorsStatus =
            mutableStateOf<DataRequestState<String>>(DataRequestState.Idle)
        val myValidatorsStatus: State<DataRequestState<String>> = _myValidatorsStatus
        private val _addRemoveValidatorStatus =
            mutableStateOf<DataRequestState<String>>(DataRequestState.Idle)
        val addRemoveValidatorStatus: State<DataRequestState<String>> = _addRemoveValidatorStatus

        private val sensorManager: SensorManager
        private val sensor: Sensor?
        private val rotation =
            floatArrayOf(
                1.0f, 0.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f, 0.0f,
            )
        private val _orientation = floatArrayOf(0.0f, 0.0f, 0.0f)

        var network by mutableStateOf<Network?>(null)
            private set
        var validator by mutableStateOf<ValidatorDetails?>(null)
            private set
        var oneKVNominators by mutableStateOf<List<OneKVNominatorSummary>>(listOf())
            private set
        private lateinit var initialOrientation: Float3
        var orientation by mutableStateOf(Float3(0.0f, 0.0f, 0.0f))
            private set

        init {
            sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
            if (sensor == null) {
                Timber.e("Rotation vector sensor not found.")
            }
        }

        fun startSensor() {
            sensorManager.registerListener(this, sensor, 100_000)
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
                    _orientation,
                )
                val x = (-_orientation[1] * 180 / Math.PI).toFloat()
                val y = ((-_orientation[2] * 180 / Math.PI) - 90).toFloat()
                // val z = ((_orientation[0] * 180 / Math.PI) + 90).toFloat()
                if (!::initialOrientation.isInitialized) {
                    initialOrientation =
                        Float3(
                            x,
                            y,
                            0.0f,
                        )
                }
                orientation =
                    Float3(
                        x - initialOrientation.x,
                        y - initialOrientation.y,
                        0.0f,
                    )
            }
        }

        fun subscribe() {
            viewModelScope.launch(Dispatchers.IO) {
                val network = networkRepository.findById(networkId)!!
                this@ValidatorDetailsViewModel.network = network
                getOneKVNominators()
                validatorDetailsService.subscribe(
                    network.validatorDetailsServiceHost!!,
                    network.validatorDetailsServicePort!!,
                    listOf(accountId.toString()),
                )
            }
        }

        private suspend fun getOneKVNominators() {
            val reportService =
                ReportService(
                    "https://${this.network!!.reportServiceHost!!}:${this.network!!.reportServicePort!!}",
                )
            try {
                val result = reportService.getOneKVNominatorSummaries()
                this.oneKVNominators = result.getOrNull() ?: this.oneKVNominators
            } catch (e: Throwable) {
                Timber.e("Error while getting 1KV nominators.", e)
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
            if (_myValidatorsStatus.value is DataRequestState.Loading ||
                _myValidatorsStatus.value is DataRequestState.Success
            ) {
                return
            }
            myValidators.clear()
            _myValidatorsStatus.value = DataRequestState.Loading
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val result = appService.getUserValidators()
                    if (result.isSuccess) {
                        myValidators.addAll(result.getOrNull() ?: listOf())
                        _isMyValidator.value =
                            myValidators.count { it.validatorAccountId == accountId } > 0
                        _myValidatorsStatus.value = DataRequestState.Success("")
                    } else {
                        _myValidatorsStatus.value = DataRequestState.Error(result.exceptionOrNull())
                    }
                } catch (e: Throwable) {
                    _myValidatorsStatus.value = DataRequestState.Error(e)
                }
            }
        }

        fun addValidator() {
            if (_addRemoveValidatorStatus.value is DataRequestState.Loading) {
                return
            }
            _addRemoveValidatorStatus.value = DataRequestState.Loading
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val request = NewUserValidator(networkId, accountId)
                    val result = appService.createUserValidator(request)
                    if (result.isSuccess) {
                        val userValidator = result.getOrNull()
                        if (userValidator != null) {
                            myValidators.add(userValidator)
                            _isMyValidator.value = true
                            _addRemoveValidatorStatus.value = DataRequestState.Success("")
                        } else {
                            _addRemoveValidatorStatus.value =
                                DataRequestState.Error(result.exceptionOrNull())
                        }
                    } else {
                        _addRemoveValidatorStatus.value =
                            DataRequestState.Error(result.exceptionOrNull())
                    }
                } catch (e: Throwable) {
                    _addRemoveValidatorStatus.value = DataRequestState.Error(e)
                }
            }
        }

        fun removeValidator() {
            if (_addRemoveValidatorStatus.value is DataRequestState.Loading) {
                return
            }
            _addRemoveValidatorStatus.value = DataRequestState.Loading
            viewModelScope.launch(Dispatchers.IO) {
                myValidators.firstOrNull { it.validatorAccountId == accountId }?.let {
                    try {
                        val result = appService.deleteUserValidator(it.id)
                        if (result.isSuccess) {
                            myValidators.removeAll { myValidator -> myValidator.validatorAccountId == accountId }
                            _isMyValidator.value = false
                            _addRemoveValidatorStatus.value = DataRequestState.Success("")
                        } else {
                            _addRemoveValidatorStatus.value =
                                DataRequestState.Error(result.exceptionOrNull())
                        }
                    } catch (e: Throwable) {
                        _addRemoveValidatorStatus.value = DataRequestState.Error(e)
                    }
                }
            }
        }
    }
