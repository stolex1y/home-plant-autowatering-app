package ru.filimonov.hpa.data.remote.model.device

import ru.filimonov.hpa.domain.model.device.DomainDevice
import java.net.URI
import java.util.Calendar
import java.util.UUID

data class DeviceResponse(
    val uuid: UUID,
    val plantId: UUID?,
    val createdDate: Calendar,
    val name: String,
    val photo: URI?,
    val mac: String,
) {
    fun toDomain() = DomainDevice(
        uuid = uuid,
        plantId = plantId,
        createdDate = createdDate,
        photoUri = photo,
        name = name,
        mac = mac,
    )
}
