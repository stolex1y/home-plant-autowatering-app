package ru.filimonov.hpa.domain.service

import kotlinx.coroutines.flow.Flow
import ru.filimonov.hpa.domain.model.DomainPlant
import java.util.UUID

interface PlantService {
    fun getAllInList(ids: List<UUID>): Flow<Result<List<DomainPlant>>>
    suspend fun add(plant: DomainPlant): Result<DomainPlant>
    suspend fun delete(uuid: UUID): Result<Unit>
    suspend fun update(plant: DomainPlant): Result<Unit>
}
