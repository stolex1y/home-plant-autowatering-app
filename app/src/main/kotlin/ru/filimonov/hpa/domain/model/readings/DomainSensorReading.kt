package ru.filimonov.hpa.domain.model.readings

import java.time.ZonedDateTime

data class DomainSensorReading<ReadingType>(
    val reading: ReadingType,
    val timestamp: ZonedDateTime,
)
