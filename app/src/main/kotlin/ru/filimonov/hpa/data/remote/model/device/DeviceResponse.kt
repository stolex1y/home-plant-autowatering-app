package ru.filimonov.hpa.data.remote.model.device

import ru.filimonov.hpa.domain.model.Device
import java.util.Calendar
import java.util.UUID

data class DeviceResponse(
    val uuid: UUID,
    val plantId: UUID?,
    val createdDate: Calendar,
) {
    fun toDomain() = Device(
        uuid = uuid,
        plantId = plantId,
        createdDate = createdDate,
    )
}
