package ru.filimonov.hpa.domain.model

import java.util.UUID

data class DomainPlant(
    val uuid: UUID,
    val name: String,
    val photo: UUID?,
    val airTempMin: Float?,
    val airTempMax: Float?,
    val airHumidityMin: Float?,
    val airHumidityMax: Float?,
    val soilMoistureMin: Float?,
    val soilMoistureMax: Float?,
    val lightLuxMax: Int?,
    val lightLuxMin: Int?,
)
