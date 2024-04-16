package ru.filimonov.hpa.data.remote.model.plant

import ru.filimonov.hpa.domain.model.DomainPlant
import java.util.UUID

data class PlantResponse(
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
) {
    fun toDomain() = DomainPlant(
        uuid = uuid,
        name = name,
        photo = photo,
        airTempMin = airTempMin,
        airTempMax = airTempMax,
        airHumidityMin = airHumidityMin,
        airHumidityMax = airHumidityMax,
        soilMoistureMin = soilMoistureMin,
        soilMoistureMax = soilMoistureMax,
        lightLuxMax = lightLuxMax,
        lightLuxMin = lightLuxMin,
    )
}
