package ru.filimonov.hpa.domain.model

import java.net.URI
import java.util.UUID

data class DomainPlant(
    val uuid: UUID,
    val name: String,
    val photoUri: URI?,
    val airTempMin: Float?,
    val airTempMax: Float?,
    val airHumidityMin: Float?,
    val airHumidityMax: Float?,
    val soilMoistureMin: Float?,
    val lightLuxMax: Int?,
    val lightLuxMin: Int?,
)
