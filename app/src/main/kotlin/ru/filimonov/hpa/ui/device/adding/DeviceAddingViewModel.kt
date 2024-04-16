package ru.filimonov.hpa.ui.device.adding

import androidx.annotation.StringRes
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import ru.filimonov.hpa.R
import ru.filimonov.hpa.common.coroutine.CoroutineNames
import ru.filimonov.hpa.common.coroutine.FlowExtensions.mapLatestResult
import ru.filimonov.hpa.domain.model.DeviceInfo
import ru.filimonov.hpa.domain.service.device.DeviceConfiguringService
import ru.filimonov.hpa.ui.common.udf.AbstractViewModel
import ru.filimonov.hpa.ui.common.udf.IData
import ru.filimonov.hpa.ui.common.udf.IEvent
import ru.filimonov.hpa.ui.common.udf.IState
import ru.filimonov.hpa.ui.common.work.WorkError
import ru.filimonov.hpa.ui.common.work.WorkUtils.deserialize
import ru.filimonov.hpa.ui.device.adding.model.AddingDevice
import ru.filimonov.hpa.ui.device.adding.model.AddingDeviceConfiguration
import ru.filimonov.hpa.ui.device.adding.model.AddingErrors
import java.util.UUID
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

@HiltViewModel
class DeviceAddingViewModel @Inject constructor(
    private val deviceConfigurationService: DeviceConfiguringService,
    @Named(CoroutineNames.APPLICATION_SCOPE) applicationScope: CoroutineScope,
    workManager: Provider<WorkManager>,
) : AbstractViewModel<DeviceAddingViewModel.Event, DeviceAddingViewModel.Data, DeviceAddingViewModel.State>(
    initData = Data.Empty,
    stateFactory = State.factory,
    applicationScope = applicationScope,
    workManager = workManager,
) {

    fun addDevice(deviceInfo: DeviceInfo, deviceConfiguration: AddingDeviceConfiguration) {
        if (deviceConfiguration.isNotValid) {
            updateState(IllegalStateException("device configuration must be valid"))
            return
        }
        startWork(
            DeviceAddingWorker.createWorkRequest(
                AddingDevice(
                    mac = deviceInfo.mac,
                    ssid = deviceConfiguration.ssid.value,
                    pass = deviceConfiguration.pass.value
                )
            ),
            loadingState = State.Adding,
            finishState = { State.Added(it.outputData.deserialize(UUID::class)!!) },
        )
    }

    override fun dispatchEvent(event: Event) {
        when (event) {
            is Event.Add -> {
                addDevice(event.deviceInfo, event.deviceConfiguration)
            }
        }
    }

    override fun loadData(): Flow<Result<Data.Filled>> {
        return deviceConfigurationService.getDeviceInfo().mapLatestResult {
            Data.Filled(deviceInfo = it)
        }
    }

    override fun parseWorkError(workError: WorkError): Int {
        when (workError) {
            AddingErrors.DeviceAlreadyAddedError -> return R.string.device_already_added
            AddingErrors.InvalidDeviceConfigurationError -> return R.string.invalid_device_configuration
        }
        return super.parseWorkError(workError)
    }

    sealed interface State : IState {
        data object Initial : State
        data object Loading : State
        data object Loaded : State
        data class Error(@StringRes val error: Int) : State
        data object Adding : State
        data class Added(val addedDeviceId: UUID) : State

        companion object {
            val factory = object : IState.Factory<State> {
                override val initState: State = Initial
                override val loadingState: State = Loading
                override val loadedState: State = Loaded

                override fun errorState(@StringRes error: Int): State = Error(error)
                override fun isError(state: State): Boolean = state is Error
            }
        }
    }

    sealed interface Event : IEvent {
        data class Add(
            val deviceInfo: DeviceInfo, val deviceConfiguration: AddingDeviceConfiguration
        ) : Event
    }

    sealed interface Data : IData {
        data class Filled(
            val deviceInfo: DeviceInfo
        ) : Data

        data object Empty : Data
    }
}
