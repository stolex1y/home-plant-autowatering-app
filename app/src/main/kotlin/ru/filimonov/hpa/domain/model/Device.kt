package ru.filimonov.hpa.domain.model

import java.util.Calendar
import java.util.UUID

data class Device(
    val uuid: UUID,
    val plantId: UUID?,
    val createdDate: Calendar,
)
