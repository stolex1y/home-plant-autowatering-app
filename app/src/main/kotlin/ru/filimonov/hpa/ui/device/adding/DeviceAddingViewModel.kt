package ru.filimonov.hpa.ui.device.adding

import androidx.annotation.StringRes
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import ru.filimonov.hpa.R
import ru.filimonov.hpa.common.coroutine.CoroutineNames
import ru.filimonov.hpa.common.coroutine.FlowExtensions.mapLatestResult
import ru.filimonov.hpa.domain.model.device.DomainDeviceInfo
import ru.filimonov.hpa.domain.service.device.DeviceConfiguringService
import ru.filimonov.hpa.ui.common.udf.AbstractViewModel
import ru.filimonov.hpa.ui.common.udf.IData
import ru.filimonov.hpa.ui.common.udf.IEvent
import ru.filimonov.hpa.ui.common.udf.IUiState
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
) : AbstractViewModel<DeviceAddingViewModel.Event, DeviceAddingViewModel.Data, DeviceAddingViewModel.UiState>(
    initData = Data.Empty,
    stateFactory = UiState.factory,
    applicationScope = applicationScope,
    workManager = workManager,
) {

    fun addDevice(domainDeviceInfo: DomainDeviceInfo, deviceConfiguration: AddingDeviceConfiguration) {
        if (deviceConfiguration.isNotValid) {
            updateState(IllegalStateException("device configuration must be valid"))
            return
        }
        startWork(
            DeviceAddingWorker.createWorkRequest(
                AddingDevice(
                    mac = domainDeviceInfo.mac,
                    ssid = deviceConfiguration.ssid.value,
                    pass = deviceConfiguration.pass.value,
                    name = domainDeviceInfo.mac,
                )
            ),
            loadingState = UiState.Adding,
            finishState = { UiState.Added(it.outputData.deserialize(UUID::class)!!) },
        )
    }

    override fun dispatchEvent(event: Event) {
        when (event) {
            is Event.Add -> {
                addDevice(event.domainDeviceInfo, event.deviceConfiguration)
            }
        }
    }

    override fun loadData(): Flow<Result<Data.Filled>> {
        return deviceConfigurationService.getDeviceInfo().mapLatestResult {
            Data.Filled(domainDeviceInfo = it)
        }
    }

    override fun parseWorkError(workError: WorkError): Int {
        when (workError) {
            AddingErrors.DeviceAlreadyAddedError -> return R.string.device_already_added
            AddingErrors.InvalidDeviceConfigurationError -> return R.string.invalid_device_configuration
        }
        return super.parseWorkError(workError)
    }

    sealed interface UiState : IUiState {
        data object Initial : UiState
        data object Loading : UiState
        data object Loaded : UiState
        data class Error(@StringRes val error: Int) : UiState
        data object Adding : UiState
        data class Added(val addedDeviceId: UUID) : UiState

        companion object {
            val factory = object : IUiState.Factory<UiState> {
                override val initState: UiState = Initial
                override val loadingState: UiState = Loading
                override val loadedState: UiState = Loaded

                override fun errorState(@StringRes error: Int): UiState = Error(error)
                override fun isError(state: UiState): Boolean = state is Error
            }
        }
    }

    sealed interface Event : IEvent {
        data class Add(
            val domainDeviceInfo: DomainDeviceInfo, val deviceConfiguration: AddingDeviceConfiguration
        ) : Event
    }

    sealed interface Data : IData {
        data class Filled(
            val domainDeviceInfo: DomainDeviceInfo
        ) : Data

        data object Empty : Data
    }
}
