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
import ru.filimonov.hpa.data.remote.mapExceptionToDomain
import ru.filimonov.hpa.data.remote.mapLatestResultExceptionToDomain
import ru.filimonov.hpa.data.remote.model.plant.PlantResponse
import ru.filimonov.hpa.data.remote.model.plant.toAddPlantRequest
import ru.filimonov.hpa.data.remote.model.plant.toUpdatePlantRequest
import ru.filimonov.hpa.data.remote.repository.PlantRemoteRepository
import ru.filimonov.hpa.domain.model.DomainPlant
import ru.filimonov.hpa.domain.service.PlantService
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Named

class PlantServiceImpl @Inject constructor(
    private val plantRemoteRepository: PlantRemoteRepository,
    @Named(CoroutineNames.IO_DISPATCHER) private val dispatcher: CoroutineDispatcher,
) : PlantService {
    override fun getAllInList(ids: List<UUID>): Flow<Result<List<DomainPlant>>> = makeSyncFlow {
        plantRemoteRepository.getAllInList(ids)
            .map(PlantResponse::toDomain)
    }
        .onStart { Timber.d("start getting plants by ids") }
        .distinctUntilChanged()
        .onEach { Timber.v("get new plants by ids") }
        .mapLatestResultExceptionToDomain()
        .flowOn(dispatcher)

    override suspend fun add(plant: DomainPlant): Result<DomainPlant> = runCatching {
        withContext(dispatcher) {
            Timber.d("add new plant")
            val addedPlant = plantRemoteRepository.add(plant.toAddPlantRequest()).toDomain()
            Timber.d("successfully added")
            addedPlant
        }
    }.mapExceptionToDomain()

    override suspend fun delete(uuid: UUID): Result<Unit> = runCatching {
        withContext(dispatcher) {
            Timber.d("add new plant")
            plantRemoteRepository.delete(uuid)
            Timber.d("successfully added")
        }
    }.mapExceptionToDomain()

    override suspend fun update(plant: DomainPlant): Result<Unit> = runCatching {
        withContext(dispatcher) {
            Timber.d("update plant with id - ${plant.uuid}")
            plantRemoteRepository.update(plant.uuid, plant.toUpdatePlantRequest()).toDomain()
            Timber.d("successfully updated")
        }
    }.mapExceptionToDomain()
}
