package ru.filimonov.hpa.data.remote.model.device

import ru.filimonov.hpa.domain.model.Device
import java.util.UUID

data class UpdateDeviceRequest(
    val uuid: UUID,
    val plantId: UUID? = null
)

fun Device.toUpdateDeviceRequest() = UpdateDeviceRequest(
    uuid = uuid,
    plantId = plantId,
)
