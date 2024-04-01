package ru.filimonov.hpa.data.remote.model.device

import ru.filimonov.hpa.domain.model.ExtendedDevice
import java.util.UUID

data class AddDeviceRequest(
    val mac: String,
    val plantId: UUID? = null,
)

fun ExtendedDevice.toAddDeviceRequest() = AddDeviceRequest(
    mac = mac,
    plantId = plantId
)
