package ru.filimonov.hpa.domain.model.device

import java.net.URL
import java.util.UUID

data class DomainDeviceConfiguration(
    val ssid: String,
    val pass: String,
    val deviceId: UUID,
    val serverUrl: URL,
)
