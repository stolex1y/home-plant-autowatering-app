package ru.filimonov.hpa.domain.service.device

import kotlinx.coroutines.flow.Flow
import ru.filimonov.hpa.domain.model.device.DomainDeviceConfiguration
import ru.filimonov.hpa.domain.model.device.DomainDeviceInfo

interface DeviceConfiguringService {
    fun getDeviceInfo(): Flow<Result<DomainDeviceInfo>>
    suspend fun sendConfiguration(deviceConfiguration: DomainDeviceConfiguration): Result<Unit>
}
