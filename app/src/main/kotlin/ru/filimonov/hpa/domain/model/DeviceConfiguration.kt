package ru.filimonov.hpa.domain.model

import java.net.URL
import java.util.UUID

data class DeviceConfiguration(
    val ssid: String,
    val pass: String,
    val deviceId: UUID,
    val serverUrl: URL,
)
