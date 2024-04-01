package ru.filimonov.hpa.domain.service.device

import kotlinx.coroutines.flow.Flow
import ru.filimonov.hpa.domain.model.Plant
import java.util.UUID

interface PlantService {
    fun getAllInList(ids: List<UUID>): Flow<Result<List<Plant>>>
    suspend fun add(plant: Plant): Result<Plant>
    suspend fun delete(uuid: UUID): Result<Unit>
    suspend fun update(plant: Plant): Result<Unit>
}
