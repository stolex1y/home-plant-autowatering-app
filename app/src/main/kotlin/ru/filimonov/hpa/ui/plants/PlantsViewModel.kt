package ru.filimonov.hpa.ui.plants

import android.graphics.Bitmap
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
import ru.filimonov.hpa.common.coroutine.FlowExtensions.mapLatestResult
import ru.filimonov.hpa.domain.service.PlantService
import ru.filimonov.hpa.ui.common.udf.AbstractViewModel
import ru.filimonov.hpa.ui.common.udf.IData
import ru.filimonov.hpa.ui.common.udf.IEvent
import ru.filimonov.hpa.ui.common.udf.SimpleLoadingUiState
import ru.filimonov.hpa.ui.plants.model.Plant
import ru.filimonov.hpa.ui.plants.model.Plant.Companion.toPlant
import java.net.URI
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

@HiltViewModel
class PlantsViewModel @Inject constructor(
    private val plantService: PlantService,
    @Named(DEFAULT_DISPATCHER) private val defaultDispatcher: CoroutineDispatcher,
    @Named(APPLICATION_SCOPE) applicationScope: CoroutineScope,
    workManager: Provider<WorkManager>,
) : AbstractViewModel<PlantsViewModel.Event, PlantsViewModel.Data, SimpleLoadingUiState>(
    initData = Data(),
    stateFactory = SimpleLoadingUiState.factory,
    applicationScope = applicationScope,
    workManager = workManager,
) {

    fun loadPlantPhoto(uri: URI?): Flow<Bitmap?> {
        if (uri == null) {
            return emptyFlow()
        }
        return plantService.getPhoto(uri).handleError()
    }

    override fun dispatchEvent(event: Event) {
        when (event) {
            Event.Reload -> reloadData()
        }
    }

    override fun loadData(): Flow<Result<Data>> {
        return plantService.getAll()
            .mapLatestResult { domainPlants ->
                val plants = domainPlants.map { domainPlant -> domainPlant.toPlant() }
                Data(plants = plants)
            }
            .distinctUntilChanged()
            .flowOn(defaultDispatcher)
    }

    sealed interface Event : IEvent {
        data object Reload : Event
    }

    data class Data(
        val plants: List<Plant> = emptyList()
    ) : IData
}
