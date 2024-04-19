package ru.filimonov.hpa.domain.model.device

import java.net.URI
import java.util.Calendar
import java.util.UUID

data class DomainDevice(
    val uuid: UUID = UUID.randomUUID(),
    val plantId: UUID? = null,
    val createdDate: Calendar = Calendar.getInstance(),
    val name: String,
    val photoUri: URI? = null,
    val mac: String,
)
