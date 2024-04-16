package ru.filimonov.hpa.data.remote.model.plant

import ru.filimonov.hpa.domain.model.DomainPlant

data class AddPlantRequest(
    val name: String,
    val airTempMin: Float?,
    val airTempMax: Float?,
    val airHumidityMin: Float?,
    val airHumidityMax: Float?,
    val soilMoistureMin: Float?,
    val soilMoistureMax: Float?,
    val lightLuxMax: Int?,
    val lightLuxMin: Int?,
)

fun DomainPlant.toAddPlantRequest() = AddPlantRequest(
    name = name,
    airTempMin = airTempMin,
    airTempMax = airTempMax,
    airHumidityMin = airHumidityMin,
    airHumidityMax = airHumidityMax,
    soilMoistureMin = soilMoistureMin,
    soilMoistureMax = soilMoistureMax,
    lightLuxMax = lightLuxMax,
    lightLuxMin = lightLuxMin,
)
