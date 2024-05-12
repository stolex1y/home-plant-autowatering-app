package ru.filimonov.hpa.domain.model.device

import java.util.UUID

data class DomainDeviceConfiguration(
    val wifiSsid: String,
    val wifiPass: String,
    val deviceId: UUID,
    val mqttUrl: String,
    val mqttUsername: String,
    val mqttPassword: String,
)
