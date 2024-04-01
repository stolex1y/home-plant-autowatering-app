package ru.filimonov.hpa.domain.model

import java.util.Calendar
import java.util.UUID

data class ExtendedDevice(
    val uuid: UUID,
    val mac: String,
    val plantId: UUID?,
    val createdDate: Calendar,
)
