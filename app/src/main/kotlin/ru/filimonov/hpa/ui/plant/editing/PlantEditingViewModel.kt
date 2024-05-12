package ru.filimonov.hpa.ui.plant.editing

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
import kotlinx.coroutines.flow.flowOn
import ru.filimonov.hpa.common.coroutine.CoroutineNames
import ru.filimonov.hpa.common.coroutine.FlowExtensions.mapLatestResult
import ru.filimonov.hpa.common.utils.isLocalResource
import ru.filimonov.hpa.domain.errors.NotFoundException
import ru.filimonov.hpa.domain.service.PlantService
import ru.filimonov.hpa.ui.common.udf.AbstractViewModel
import ru.filimonov.hpa.ui.common.udf.IData
import ru.filimonov.hpa.ui.common.udf.IEvent
import ru.filimonov.hpa.ui.common.udf.IUiState
import ru.filimonov.hpa.ui.plant.editing.model.EditingPlant
import ru.filimonov.hpa.ui.plant.editing.model.Plant
import ru.filimonov.hpa.ui.plant.editing.model.toPlant
import java.net.URI
import java.util.UUID
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

@HiltViewModel
class PlantEditingViewModel @Inject constructor(
    private val plantService: PlantService,
    @Named(CoroutineNames.DEFAULT_DISPATCHER) private val defaultDispatcher: CoroutineDispatcher,
    savedStateHandle: SavedStateHandle,
    @Named(CoroutineNames.APPLICATION_SCOPE) applicationScope: CoroutineScope,
    workManager: Provider<WorkManager>,
) : AbstractViewModel<PlantEditingViewModel.Event, PlantEditingViewModel.Data, PlantEditingViewModel.UiState>(
    initData = Data.Empty,
    stateFactory = UiState.factory,
    applicationScope = applicationScope,
    workManager = workManager,
) {
    private val plantId: UUID =
        savedStateHandle.get<String>(PlantEditingScreenDestination.ARG_PLANT_ID)!!
            .run(UUID::fromString)

    fun loadPlantPhoto(uri: URI?): Flow<Bitmap?> {
        if (uri == null) {
            return emptyFlow()
        }
        if (uri.isLocalResource()) {
            return plantService.getPhoto(uri).handleError()
        }
        return flow {
            val first = plantService.getPhoto(uri).handleError().first()
            emit(first)
        }
    }

    override fun dispatchEvent(event: Event) {
        when (event) {
            Event.Reload -> reloadData()
            is Event.Edit -> editPlant(event.editingPlant)
        }
    }

    override fun loadData(): Flow<Result<Data>> {
        return flow {
            val first = plantService.get(plantId).mapLatestResult {
                Data.Loaded(it.toPlant())
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

    private fun editPlant(editingPlant: EditingPlant) {
        if (editingPlant.isNotValid) {
            updateState(IllegalStateException("editing plant must be valid"))
            return
        }
        startWork(
            workRequest = PlantEditingWorker.createWorkRequest(editingPlant.toDomain()),
            loadingState = UiState.Editing,
            finishState = { UiState.Edited }
        )
    }

    sealed interface Event : IEvent {
        data object Reload : Event
        data class Edit(
            val editingPlant: EditingPlant
        ) : Event
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
        data object NotFound : UiState
        data object Editing : UiState
        data object Edited : UiState

        companion object {
            val factory =
                IUiState.createFactory(loaded = Loaded, loading = Loading, error = ::Error)
        }
    }
}
