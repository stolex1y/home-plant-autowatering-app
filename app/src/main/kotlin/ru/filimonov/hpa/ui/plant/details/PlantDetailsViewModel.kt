package ru.filimonov.hpa.ui.plant.details

import android.graphics.Bitmap
import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOn
import ru.filimonov.hpa.common.coroutine.CoroutineNames
import ru.filimonov.hpa.common.coroutine.FlowExtensions.mapLatestResult
import ru.filimonov.hpa.domain.errors.NotFoundException
import ru.filimonov.hpa.domain.service.PlantService
import ru.filimonov.hpa.ui.common.udf.AbstractViewModel
import ru.filimonov.hpa.ui.common.udf.IData
import ru.filimonov.hpa.ui.common.udf.IEvent
import ru.filimonov.hpa.ui.common.udf.IUiState
import ru.filimonov.hpa.ui.plant.details.model.Plant
import ru.filimonov.hpa.ui.plant.details.model.Plant.Companion.toPlant
import ru.filimonov.hpa.ui.plant.editing.PlantDeletingWorker
import java.net.URI
import java.util.UUID
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

@HiltViewModel
class PlantDetailsViewModel @Inject constructor(
    private val plantService: PlantService,
    @Named(CoroutineNames.DEFAULT_DISPATCHER) private val defaultDispatcher: CoroutineDispatcher,
    savedStateHandle: SavedStateHandle,
    @Named(CoroutineNames.APPLICATION_SCOPE) applicationScope: CoroutineScope,
    workManager: Provider<WorkManager>,
) : AbstractViewModel<PlantDetailsViewModel.Event, PlantDetailsViewModel.Data, PlantDetailsViewModel.UiState>(
    initData = Data.Empty,
    stateFactory = UiState.factory,
    applicationScope = applicationScope,
    workManager = workManager,
) {
    private val plantId: UUID =
        savedStateHandle.get<String>(PlantDetailsScreenDestination.ARG_PLANT_ID)!!
            .run(UUID::fromString)

    fun loadPlantPhoto(uri: URI?): Flow<Bitmap?> {
        if (uri == null) {
            return emptyFlow()
        }
        return plantService.getPhoto(uri).handleError()
    }

    override fun dispatchEvent(event: Event) {
        when (event) {
            Event.Reload -> reloadData()
            Event.Delete -> deletePlant()
        }
    }

    override fun loadData(): Flow<Result<Data>> {
        return plantService.get(plantId).mapLatestResult {
            Data.Loaded(it.toPlant())
        }.flowOn(defaultDispatcher)
    }

    override fun updateState(error: Throwable) {
        when (error) {
            is NotFoundException -> updateState(UiState.NotFound)
            else -> super.updateState(error)
        }
    }

    private fun deletePlant() {
        startWork(
            PlantDeletingWorker.createWorkRequest(plantId),
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
            val plant: Plant
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
