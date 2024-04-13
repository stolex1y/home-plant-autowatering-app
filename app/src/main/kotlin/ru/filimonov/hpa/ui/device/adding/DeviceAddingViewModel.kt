package ru.filimonov.hpa.ui.device.adding

import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import ru.filimonov.hpa.common.coroutine.CoroutineNames
import ru.filimonov.hpa.common.coroutine.FlowExtensions.mapLatestResult
import ru.filimonov.hpa.domain.model.DeviceInfo
import ru.filimonov.hpa.domain.service.device.DeviceConfiguringService
import ru.filimonov.hpa.ui.common.udf.AbstractViewModel
import ru.filimonov.hpa.ui.common.udf.IData
import ru.filimonov.hpa.ui.common.udf.IEvent
import ru.filimonov.hpa.ui.common.udf.SimpleLoadingState
import ru.filimonov.hpa.ui.device.adding.model.AddingDevice
import ru.filimonov.hpa.ui.device.adding.model.AddingDeviceConfiguration
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

@HiltViewModel
class DeviceAddingViewModel @Inject constructor(
    private val deviceConfigurationService: DeviceConfiguringService,
    @Named(CoroutineNames.APPLICATION_SCOPE) applicationScope: CoroutineScope,
    workManager: Provider<WorkManager>,
) : AbstractViewModel<DeviceAddingViewModel.Event, DeviceAddingViewModel.Data, SimpleLoadingState>(
    initData = Data.Empty,
    stateFactory = SimpleLoadingState.factory,
    applicationScope = applicationScope,
    workManager = workManager,
) {

    fun addDevice(deviceInfo: DeviceInfo, deviceConfiguration: AddingDeviceConfiguration) {
        if (deviceConfiguration.isNotValid) {
            updateState(IllegalStateException("device configuration must be valid"))
            return
        }
        dispatchEvent(
            Event.Add(
                AddingDevice(
                    mac = deviceInfo.mac,
                    ssid = deviceConfiguration.ssid.value,
                    pass = deviceConfiguration.pass.value
                )
            )
        )
    }

    override fun dispatchEvent(event: Event) {
        when (event) {
            is Event.Add -> startWork(
                DeviceAddingWorker.createWorkRequest(event.addingDevice),
                SimpleLoadingState.Loaded
            )
        }
    }

    override fun loadData(): Flow<Result<Data.Filled>> {
        return deviceConfigurationService.getDeviceInfo().mapLatestResult {
            Data.Filled(deviceInfo = it)
        }
    }

    sealed interface Event : IEvent {
        data class Add(
            val addingDevice: AddingDevice
        ) : Event
    }

    sealed interface Data : IData {
        data class Filled(
            val deviceInfo: DeviceInfo
        ) : Data

        data object Empty : Data
    }
}
