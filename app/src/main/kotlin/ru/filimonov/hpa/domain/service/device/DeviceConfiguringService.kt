package ru.filimonov.hpa.domain.service.device

import kotlinx.coroutines.flow.Flow
import ru.filimonov.hpa.domain.model.DeviceConfiguration
import ru.filimonov.hpa.domain.model.DeviceInfo

interface DeviceConfiguringService {
    fun getDeviceInfo(): Flow<Result<DeviceInfo>>
    suspend fun sendConfiguration(deviceConfiguration: DeviceConfiguration): Result<Unit>
}
