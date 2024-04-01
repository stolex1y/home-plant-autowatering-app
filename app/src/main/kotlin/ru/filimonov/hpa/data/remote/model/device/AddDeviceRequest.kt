package ru.filimonov.hpa.data.remote.model.device

import java.util.UUID

data class DeviceRequest(
    val mac: String,
    val plantId: UUID? = null,
)
