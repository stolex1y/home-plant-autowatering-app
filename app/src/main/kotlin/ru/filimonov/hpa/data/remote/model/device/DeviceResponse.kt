package ru.filimonov.hpa.data.remote.model.device

import ru.filimonov.hpa.domain.model.device.DomainDevice
import java.util.Calendar
import java.util.UUID

data class DeviceResponse(
    val uuid: UUID,
    val plantId: UUID?,
    val createdDate: Calendar,
    val name: String,
    val photoId: UUID?,
    val mac: String,
) {
    fun toDomain() = DomainDevice(
        uuid = uuid,
        plantId = plantId,
        createdDate = createdDate,
        photoId = photoId,
        name = name,
        mac = mac,
    )
}
