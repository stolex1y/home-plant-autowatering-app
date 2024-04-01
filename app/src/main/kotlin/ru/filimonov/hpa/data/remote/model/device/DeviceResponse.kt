package ru.filimonov.hpa.data.remote.model

import ru.filimonov.hpa.domain.model.Device
import java.util.UUID

data class DeviceResponse(
    val uuid: UUID,
    val plantId: UUID?,
    val createdDate: Long,
) {
    fun toDomain() = Device(
        uuid = uuid,
        plantId = plantId,
        createdDate = createdDate,
    )
}
