package ru.filimonov.hpa.data.service

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import ru.filimonov.hpa.common.coroutine.CoroutineNames
import ru.filimonov.hpa.common.coroutine.FlowExtensions.makeSyncFlow
import ru.filimonov.hpa.data.remote.model.plant.PlantResponse
import ru.filimonov.hpa.data.remote.model.plant.toAddPlantRequest
import ru.filimonov.hpa.data.remote.model.plant.toUpdatePlantRequest
import ru.filimonov.hpa.data.remote.repository.PlantRemoteRepository
import ru.filimonov.hpa.domain.model.Plant
import ru.filimonov.hpa.domain.service.device.PlantService
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Named

class PlantServiceImpl @Inject constructor(
    private val plantRemoteRepository: PlantRemoteRepository,
    @Named(CoroutineNames.IO_DISPATCHER) private val dispatcher: CoroutineDispatcher,
) : PlantService {
    override fun getAllInList(ids: List<UUID>): Flow<Result<List<Plant>>> = makeSyncFlow {
        plantRemoteRepository.getAllInList(ids)
            .map(PlantResponse::toDomain)
    }
        .onStart { Timber.d("start getting plants by ids") }
        .distinctUntilChanged()
        .onEach { Timber.v("get new plants by ids") }
        .flowOn(dispatcher)

    override suspend fun add(plant: Plant): Result<Plant> = runCatching {
        withContext(dispatcher) {
            Timber.d("add new plant")
            val addedPlant = plantRemoteRepository.add(plant.toAddPlantRequest()).toDomain()
            Timber.d("successfully added")
            addedPlant
        }
    }

    override suspend fun delete(uuid: UUID): Result<Unit> = runCatching {
        withContext(dispatcher) {
            Timber.d("add new plant")
            plantRemoteRepository.delete(uuid)
            Timber.d("successfully added")
        }
    }

    override suspend fun update(plant: Plant): Result<Unit> = runCatching {
        withContext(dispatcher) {
            Timber.d("update plant with id - ${plant.uuid}")
            plantRemoteRepository.update(plant.uuid, plant.toUpdatePlantRequest()).toDomain()
            Timber.d("successfully updated")
        }
    }
}
