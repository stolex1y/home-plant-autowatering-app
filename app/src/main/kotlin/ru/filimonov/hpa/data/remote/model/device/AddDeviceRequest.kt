package ru.filimonov.hpa.data.remote.model.device

import ru.filimonov.hpa.domain.model.device.DomainDevice

data class AddDeviceRequest(
    val mac: String,
)

fun DomainDevice.toAddDeviceRequest() = AddDeviceRequest(
    mac = mac,
)
