package ru.filimonov.hpa.domain.service.device

import kotlinx.coroutines.flow.Flow
import ru.filimonov.hpa.domain.model.Device
import ru.filimonov.hpa.domain.model.ExtendedDevice
import java.util.UUID

interface DeviceService {
    fun getAll(): Flow<Result<List<Device>>>
    suspend fun add(device: ExtendedDevice): Result<Device>
    suspend fun delete(uuid: UUID): Result<Unit>
    suspend fun update(device: Device): Result<Unit>
}
