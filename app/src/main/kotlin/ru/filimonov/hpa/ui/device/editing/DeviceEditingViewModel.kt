package ru.filimonov.hpa.ui.device.editing

import android.graphics.Bitmap
import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import ru.filimonov.hpa.common.coroutine.CoroutineNames
import ru.filimonov.hpa.common.coroutine.FlowExtensions.combineFlowResultsTransform
import ru.filimonov.hpa.common.coroutine.FlowExtensions.flatMapLatestResult
import ru.filimonov.hpa.common.coroutine.FlowExtensions.mapLatestResultList
import ru.filimonov.hpa.common.utils.isLocalResource
import ru.filimonov.hpa.domain.errors.NotFoundException
import ru.filimonov.hpa.domain.service.PlantService
import ru.filimonov.hpa.domain.service.device.DeviceService
import ru.filimonov.hpa.ui.common.udf.AbstractViewModel
import ru.filimonov.hpa.ui.common.udf.IData
import ru.filimonov.hpa.ui.common.udf.IEvent
import ru.filimonov.hpa.ui.common.udf.IUiState
import ru.filimonov.hpa.ui.device.details.DeviceDetailsScreenDestination
import ru.filimonov.hpa.ui.device.editing.model.Device
import ru.filimonov.hpa.ui.device.editing.model.Device.Companion.toDevice
import ru.filimonov.hpa.ui.device.editing.model.EditingDevice
import ru.filimonov.hpa.ui.device.editing.model.Plant
import ru.filimonov.hpa.ui.device.editing.model.Plant.Companion.toPlant
import java.net.URI
import java.util.UUID
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

@HiltViewModel
class DeviceEditingViewModel @Inject constructor(
    private val deviceService: DeviceService,
    private val plantService: PlantService,
    @Named(CoroutineNames.DEFAULT_DISPATCHER) private val defaultDispatcher: CoroutineDispatcher,
    private val savedStateHandle: SavedStateHandle,
    @Named(CoroutineNames.APPLICATION_SCOPE) applicationScope: CoroutineScope,
    workManager: Provider<WorkManager>,
) : AbstractViewModel<DeviceEditingViewModel.Event, DeviceEditingViewModel.Data, DeviceEditingViewModel.UiState>(
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
        if (uri.isLocalResource()) {
            return deviceService.getPhoto(uri).handleError()
        }
        return flow {
            val first = deviceService.getPhoto(uri).handleError().first()
            emit(first)
        }
    }

    override fun dispatchEvent(event: Event) {
        when (event) {
            Event.Reload -> reloadData()
            is Event.Edit -> editDevice(event.device)
            Event.Delete -> deleteDevice()
        }
    }

    override fun loadData(): Flow<Result<Data>> {
        val domainDevicesFlow = deviceService.get(deviceId)
        val plantsFlow = plantService.getAll().mapLatestResultList { it.toPlant() }
        val deviceDomainPlantFlow = domainDevicesFlow.flatMapLatestResult { device ->
            if (device.plantId != null)
                plantService.get(device.plantId)
            else
                flowOf(Result.success(null))
        }
        return flow {
            val first = combineFlowResultsTransform(
                domainDevicesFlow,
                deviceDomainPlantFlow,
                plantsFlow
            ) { domainDevice, deviceDomainPlant, plants ->
                Data.Loaded(
                    plants = plants,
                    device = domainDevice.toDevice(plant = deviceDomainPlant)
                )
            }.flowOn(defaultDispatcher).first()
            emit(first)
        }
    }

    override fun updateState(error: Throwable) {
        when (error) {
            is NotFoundException -> updateState(UiState.NotFound)
            else -> super.updateState(error)
        }
    }

    private fun editDevice(editingDevice: EditingDevice) {
        if (editingDevice.isNotValid) {
            updateState(IllegalStateException("editing device must be valid"))
            return
        }
        startWork(
            workRequest = DeviceEditingWorker.createWorkRequest(editingDevice.toDomain()),
            loadingState = UiState.Editing,
            finishState = { UiState.Edited }
        )
    }

    private fun deleteDevice() {
        startWork(
            workRequest = DeviceDeletingWorker.createWorkRequest(deviceId),
            loadingState = UiState.Deleting,
            finishState = { UiState.Deleted }
        )
    }

    sealed interface Event : IEvent {
        data object Reload : Event
        data class Edit(val device: EditingDevice) : Event
        data object Delete : Event
    }

    sealed interface Data : IData {
        data object Empty : Data
        data class Loaded(
            val plants: List<Plant>,
            val device: Device,
        ) : Data
    }

    sealed interface UiState : IUiState {
        data object Loading : UiState
        data object Loaded : UiState
        data class Error(@StringRes val error: Int) : UiState
        data object Editing : UiState
        data object Edited : UiState
        data object Deleting : UiState
        data object Deleted : UiState
        data object NotFound : UiState

        companion object {
            val factory =
                IUiState.createFactory(loaded = Loaded, loading = Loading, error = ::Error)
        }
    }
}
