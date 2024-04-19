package ru.filimonov.hpa.domain.service.device

import android.graphics.Bitmap
import kotlinx.coroutines.flow.Flow
import ru.filimonov.hpa.domain.model.device.DomainDevice
import java.net.URI
import java.util.UUID

interface DeviceService {
    fun getAll(): Flow<Result<List<DomainDevice>>>
    suspend fun add(device: DomainDevice): Result<DomainDevice>
    suspend fun delete(uuid: UUID): Result<Unit>
    suspend fun update(device: DomainDevice): Result<Unit>
    fun getPhoto(photoUri: URI): Flow<Result<Bitmap?>>
}
