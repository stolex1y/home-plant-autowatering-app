package ru.filimonov.hpa.data.remote.model.device

import ru.filimonov.hpa.domain.model.device.DomainDevice
import java.util.UUID

data class UpdateDeviceRequest(
    val uuid: UUID,
    val plantId: UUID? = null,
    val name: String,
)

fun DomainDevice.toUpdateDeviceRequest() = UpdateDeviceRequest(
    uuid = uuid,
    plantId = plantId,
    name = name,
)
