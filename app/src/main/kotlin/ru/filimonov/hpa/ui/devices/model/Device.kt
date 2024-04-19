package ru.filimonov.hpa.ui.devices.model

import java.util.UUID

sealed class DeviceCardData(
    val uuid: UUID,
)

class DeviceWithPlantCardData(
    deviceId: UUID,
    val plantId: UUID,
    val plantName: String,
) : DeviceCardData(uuid = deviceId)

class DeviceWithoutPlantCardData(
    deviceId: UUID
) : DeviceCardData(uuid = deviceId)
