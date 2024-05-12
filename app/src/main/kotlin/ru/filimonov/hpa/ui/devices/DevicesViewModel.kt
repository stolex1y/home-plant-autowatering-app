package ru.filimonov.hpa.ui.devices

import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOn
import ru.filimonov.hpa.common.coroutine.CoroutineNames.APPLICATION_SCOPE
import ru.filimonov.hpa.common.coroutine.CoroutineNames.DEFAULT_DISPATCHER
import ru.filimonov.hpa.common.coroutine.FlowExtensions.flatMapLatestResult
import ru.filimonov.hpa.common.coroutine.FlowExtensions.mapLatestResult
import ru.filimonov.hpa.domain.model.DomainPlant
import ru.filimonov.hpa.domain.service.PlantService
import ru.filimonov.hpa.domain.service.device.DeviceService
import ru.filimonov.hpa.ui.common.udf.AbstractViewModel
import ru.filimonov.hpa.ui.common.udf.IData
import ru.filimonov.hpa.ui.common.udf.IEvent
import ru.filimonov.hpa.ui.common.udf.SimpleLoadingUiState
import ru.filimonov.hpa.ui.devices.model.Device
import ru.filimonov.hpa.ui.devices.model.Device.Companion.toDevice
import java.net.URI
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

@HiltViewModel
class DevicesViewModel @Inject constructor(
    private val deviceService: DeviceService,
    private val plantService: PlantService,
    @Named(DEFAULT_DISPATCHER) private val defaultDispatcher: CoroutineDispatcher,
    private val savedStateHandle: SavedStateHandle,
    @Named(APPLICATION_SCOPE) applicationScope: CoroutineScope,
    workManager: Provider<WorkManager>,
) : AbstractViewModel<DevicesViewModel.Event, DevicesViewModel.Data, SimpleLoadingUiState>(
    initData = Data(),
    stateFactory = SimpleLoadingUiState.factory,
    applicationScope = applicationScope,
    workManager = workManager,
) {

    fun loadDevicePhoto(uri: URI?): Flow<Bitmap?> {
        if (uri == null) {
            return emptyFlow()
        }
        return deviceService.getPhoto(uri).handleError()
    }

    override fun dispatchEvent(event: Event) {
        when (event) {
            Event.Reload -> reloadData()
        }
    }

    override fun loadData(): Flow<Result<Data>> {
        return deviceService.getAll()
            .flatMapLatestResult { domainDevices ->
                plantService
                    .getAllInList(domainDevices.map { domainDevice -> domainDevice.uuid })
                    .mapLatestResult { domainPlants ->
                        domainPlants.associateBy(DomainPlant::uuid)
                    }
                    .mapLatestResult { plants ->
                        Data(devices = domainDevices.map { domainDevice ->
                            val plant = domainDevice.plantId?.run(plants::get)
                            domainDevice.toDevice(plant)
                        })
                    }
            }
            .distinctUntilChanged()
            .flowOn(defaultDispatcher)
    }

    sealed interface Event : IEvent {
        data object Reload : Event
    }

    data class Data(
        val devices: List<Device> = emptyList()
    ) : IData
}
