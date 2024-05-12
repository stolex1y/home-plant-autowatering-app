package ru.filimonov.hpa.ui.plant.editing.model

import ru.filimonov.hpa.domain.model.DomainPlant
import java.net.URI
import java.util.UUID

data class Plant(
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
) {
    fun toEditingPlant() = EditingPlant(
        uuid = uuid,
        name = name,
        photoUri = photoUri,
        airTempMin = airTempMin?.toString() ?: "",
        airTempMax = airTempMax?.toString() ?: "",
        airHumidityMin = airHumidityMin?.toString() ?: "",
        airHumidityMax = airHumidityMax?.toString() ?: "",
        soilMoistureMin = soilMoistureMin?.toString() ?: "",
        lightLuxMax = lightLuxMax?.toString() ?: "",
        lightLuxMin = lightLuxMin?.toString() ?: "",
    )
}

fun DomainPlant.toPlant() = Plant(
    uuid = uuid,
    name = name,
    photoUri = photoUri,
    airTempMin = airTempMin,
    airTempMax = airTempMax,
    airHumidityMin = airHumidityMin,
    airHumidityMax = airHumidityMax,
    soilMoistureMin = soilMoistureMin,
    lightLuxMax = lightLuxMax,
    lightLuxMin = lightLuxMin,
)
