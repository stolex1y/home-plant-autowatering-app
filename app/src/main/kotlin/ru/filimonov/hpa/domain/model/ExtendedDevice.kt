package ru.filimonov.hpa.domain.model

import java.util.Calendar
import java.util.UUID

data class ExtendedDevice(
    val mac: String,
    val uuid: UUID = UUID.randomUUID(),
    val plantId: UUID? = null,
    val createdDate: Calendar = Calendar.getInstance(),
)
