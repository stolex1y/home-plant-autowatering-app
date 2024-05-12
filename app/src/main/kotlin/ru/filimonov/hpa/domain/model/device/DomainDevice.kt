package ru.filimonov.hpa.domain.model.device

import java.net.URI
import java.time.ZonedDateTime
import java.util.UUID

data class DomainDevice(
    val uuid: UUID = UUID.randomUUID(),
    val plantId: UUID? = null,
    val createdDate: ZonedDateTime = ZonedDateTime.now(),
    val name: String,
    val photoUri: URI? = null,
    val mac: String,
)
