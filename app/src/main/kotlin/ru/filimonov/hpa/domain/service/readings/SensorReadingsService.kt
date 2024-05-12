package ru.filimonov.hpa.domain.service.readings

import kotlinx.coroutines.flow.Flow
import ru.filimonov.hpa.domain.model.readings.DomainSensorReadings
import java.util.UUID

interface SensorReadingsService {
    fun getAllLast(deviceId: UUID): Flow<Result<DomainSensorReadings>>
}
