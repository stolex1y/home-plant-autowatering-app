package ru.filimonov.hpa.data.remote.model.readings

import ru.filimonov.hpa.common.utils.time.DateUtils.toZonedDateTime
import ru.filimonov.hpa.domain.model.readings.DomainSensorReading
import java.time.ZonedDateTime

data class SensorReadingResponse<ReadingType>(
    val reading: ReadingType,
    val timestamp: Long,
) {
    fun toDomain() = DomainSensorReading(
        reading = reading,
        timestamp = timestamp.toZonedDateTime(),
    )
}
