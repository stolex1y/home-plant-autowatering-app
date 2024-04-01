package ru.filimonov.hpa.ui.devices

import androidx.lifecycle.SavedStateHandle
import androidx.work.WorkManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import ru.filimonov.hpa.common.coroutine.CoroutineNames.APPLICATION_SCOPE
import ru.filimonov.hpa.common.coroutine.CoroutineNames.DEFAULT_DISPATCHER
import ru.filimonov.hpa.common.coroutine.FlowExtensions.flatMapLatestResult
import ru.filimonov.hpa.common.coroutine.FlowExtensions.mapLatestResult
import ru.filimonov.hpa.domain.model.Plant
import ru.filimonov.hpa.domain.service.device.DeviceService
import ru.filimonov.hpa.domain.service.device.PlantService
import ru.filimonov.hpa.ui.common.udf.AbstractViewModel
import ru.filimonov.hpa.ui.common.udf.IData
import ru.filimonov.hpa.ui.common.udf.IEvent
import ru.filimonov.hpa.ui.common.udf.SimpleLoadingState
import ru.filimonov.hpa.ui.devices.model.DeviceCardData
import ru.filimonov.hpa.ui.devices.model.DeviceWithPlantCardData
import ru.filimonov.hpa.ui.devices.model.DeviceWithoutPlantCardData
import javax.inject.Named
import javax.inject.Provider

class DevicesViewModel(
    private val deviceService: DeviceService,
    private val plantService: PlantService,
    @Named(DEFAULT_DISPATCHER) private val defaultDispatcher: CoroutineDispatcher,
    private val savedStateHandle: SavedStateHandle,
    @Named(APPLICATION_SCOPE) applicationScope: CoroutineScope,
    workManager: Provider<WorkManager>,
) : AbstractViewModel<DevicesViewModel.Event, DevicesViewModel.Data, SimpleLoadingState>(
    initData = Data(),
    stateFactory = SimpleLoadingState.factory,
    applicationScope = applicationScope,
    workManager = workManager,
) {

    fun reloadData() = dispatchEvent(Event.Load)

    override fun dispatchEvent(event: Event) {
        when (event) {
            Event.Load -> startLoadingData()
        }
    }

    override fun loadData(): Flow<Result<Data>> {
        return deviceService.getAll()
            .flatMapLatestResult { devices ->
                val plantsFlow =
                    plantService.getAllInList(devices.map { device -> device.uuid })
                        .mapLatestResult { plants -> plants.associateBy(Plant::uuid) }
                plantsFlow.mapLatestResult { plants ->
                    Data(devices = devices.map { device ->
                        val plantId = device.plantId
                        if (plantId != null && plants[plantId] != null) {
                            DeviceWithPlantCardData(
                                deviceId = device.uuid,
                                plantId = plantId,
                                plantName = plants[plantId]!!.name,
                            )
                        } else {
                            DeviceWithoutPlantCardData(
                                deviceId = device.uuid,
                            )
                        }
                    })
                }
            }.flowOn(defaultDispatcher)
    }

    sealed interface Event : IEvent {
        data object Load : Event
    }

    data class Data(
        val devices: List<DeviceCardData> = emptyList()
    ) : IData
}