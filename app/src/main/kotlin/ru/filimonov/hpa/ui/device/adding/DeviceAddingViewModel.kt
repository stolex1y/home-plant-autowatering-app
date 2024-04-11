package ru.filimonov.hpa.ui.device.adding

import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.filimonov.hpa.common.coroutine.CoroutineNames
import ru.filimonov.hpa.domain.model.ExtendedDevice
import ru.filimonov.hpa.ui.common.udf.AbstractViewModel
import ru.filimonov.hpa.ui.common.udf.EmptyData
import ru.filimonov.hpa.ui.common.udf.IEvent
import ru.filimonov.hpa.ui.common.udf.SimpleLoadingState
import ru.filimonov.hpa.ui.device.adding.model.AddingDevice
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

@HiltViewModel
class DeviceAddingViewModel @Inject constructor(
    @Named(CoroutineNames.APPLICATION_SCOPE) applicationScope: CoroutineScope,
    workManager: Provider<WorkManager>,
) : AbstractViewModel<DeviceAddingViewModel.Event, EmptyData, SimpleLoadingState>(
    initData = EmptyData,
    stateFactory = SimpleLoadingState.factory,
    applicationScope = applicationScope,
    workManager = workManager,
) {

    fun addDevice(addingDevice: AddingDevice) {
        dispatchEvent(Event.Add(addingDevice.toExtendedDevice()))
    }

    override fun dispatchEvent(event: Event) {
        when (event) {
            is Event.Add -> startWork(
                DeviceAddingWorker.createWorkRequest(event.addingDevice),
                SimpleLoadingState.Loaded
            )
        }
    }

    override fun loadData(): Flow<Result<EmptyData>> {
        return flow {
            emit(Result.success(EmptyData))
        }
    }

    sealed interface Event : IEvent {
        data class Add(
            val addingDevice: ExtendedDevice
        ) : Event
    }
}
