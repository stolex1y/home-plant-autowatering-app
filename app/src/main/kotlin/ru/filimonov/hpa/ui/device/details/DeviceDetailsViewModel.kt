package ru.filimonov.hpa.ui.device.details

import android.graphics.Bitmap
import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import ru.filimonov.hpa.common.coroutine.CoroutineNames
import ru.filimonov.hpa.common.coroutine.FlowExtensions.combineFlowResultsTransform
import ru.filimonov.hpa.common.coroutine.FlowExtensions.flatMapLatestResult
import ru.filimonov.hpa.domain.errors.NotFoundException
import ru.filimonov.hpa.domain.service.PlantService
import ru.filimonov.hpa.domain.service.device.DeviceService
import ru.filimonov.hpa.domain.service.readings.SensorReadingsService
import ru.filimonov.hpa.ui.common.udf.AbstractViewModel
import ru.filimonov.hpa.ui.common.udf.IData
import ru.filimonov.hpa.ui.common.udf.IEvent
import ru.filimonov.hpa.ui.common.udf.IUiState
import ru.filimonov.hpa.ui.device.details.model.Device
import ru.filimonov.hpa.ui.device.details.model.Device.Companion.toDevice
import ru.filimonov.hpa.ui.device.editing.DeviceDeletingWorker
import java.net.URI
import java.util.UUID
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

@HiltViewModel
class DeviceDetailsViewModel @Inject constructor(
    private val deviceService: DeviceService,
    private val plantService: PlantService,
    private val sensorReadingsService: SensorReadingsService,
    @Named(CoroutineNames.DEFAULT_DISPATCHER) private val defaultDispatcher: CoroutineDispatcher,
    savedStateHandle: SavedStateHandle,
    @Named(CoroutineNames.APPLICATION_SCOPE) applicationScope: CoroutineScope,
    workManager: Provider<WorkManager>,
) : AbstractViewModel<DeviceDetailsViewModel.Event, DeviceDetailsViewModel.Data, DeviceDetailsViewModel.UiState>(
    initData = Data.Empty,
    stateFactory = UiState.factory,
    applicationScope = applicationScope,
    workManager = workManager,
) {
    private val deviceId: UUID =
        savedStateHandle.get<String>(DeviceDetailsScreenDestination.ARG_DEVICE_ID)!!
            .run(UUID::fromString)

    fun loadDevicePhoto(uri: URI?): Flow<Bitmap?> {
        if (uri == null) {
            return emptyFlow()
        }
        return deviceService.getPhoto(uri).handleError()
    }

    override fun dispatchEvent(event: Event) {
        when (event) {
            Event.Reload -> reloadData()
            Event.Delete -> deleteDevice()
        }
    }

    override fun loadData(): Flow<Result<Data>> {
        val deviceFlow = deviceService.get(deviceId)
        val devicePlantFlow = deviceFlow.flatMapLatestResult { device ->
            if (device.plantId != null)
                plantService.get(device.plantId)
            else
                flowOf(Result.success(null))
        }
        val sensorReadingsFlow = deviceFlow.flatMapLatestResult { device ->
            sensorReadingsService.getAllLast(device.uuid)
        }
        return combineFlowResultsTransform(
            deviceFlow,
            devicePlantFlow,
            sensorReadingsFlow,
        ) { domainDevice, deviceDomainPlant, sensorReadings ->
            Data.Loaded(
                device = domainDevice.toDevice(
                    domainPlant = deviceDomainPlant,
                    domainSensorReadings = sensorReadings
                )
            )
        }.flowOn(defaultDispatcher)
    }

    override fun updateState(error: Throwable) {
        when (error) {
            is NotFoundException -> updateState(UiState.NotFound)
            else -> super.updateState(error)
        }
    }

    private fun deleteDevice() {
        startWork(
            DeviceDeletingWorker.createWorkRequest(deviceId),
            loadingState = UiState.Deleting,
            finishState = { UiState.Deleted },
        )
    }

    sealed interface Event : IEvent {
        data object Reload : Event
        data object Delete : Event
    }

    sealed interface Data : IData {
        data object Empty : Data
        data class Loaded(
            val device: Device
        ) : Data
    }

    sealed interface UiState : IUiState {
        data object Loading : UiState
        data object Loaded : UiState
        data class Error(@StringRes val error: Int) : UiState
        data object Deleting : UiState
        data object Deleted : UiState
        data object NotFound : UiState

        companion object {
            val factory =
                IUiState.createFactory(loaded = Loaded, loading = Loading, error = ::Error)
        }
    }
}