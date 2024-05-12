package ru.filimonov.hpa.domain.service.readings

import kotlinx.coroutines.flow.Flow
import ru.filimonov.hpa.domain.model.readings.DomainSensorReading
import java.time.ZonedDateTime
import java.util.UUID

interface SoilMoistureReadingsService {
    fun getLast(deviceId: UUID): Flow<Result<DomainSensorReading<Float>>>
    fun getForPeriodByHour(
        deviceId: UUID,
        fromTimestamp: ZonedDateTime,
        toTimestamp: ZonedDateTime
    ): Flow<Result<List<DomainSensorReading<Float>>>>
}
