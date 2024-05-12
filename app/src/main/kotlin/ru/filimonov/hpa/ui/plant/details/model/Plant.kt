package ru.filimonov.hpa.ui.plant.details.model

import ru.filimonov.hpa.domain.model.DomainPlant
import ru.filimonov.hpa.ui.util.formatAsPercents
import java.net.URI
import java.util.UUID

data class Plant(
    val uuid: UUID,
    val name: String,
    val photoUri: URI?,
    val airTempConfig: AirTempConfig,
    val airHumidityConfig: AirHumidityConfig,
    val lightLuxConfig: LightLuxConfig,
    val soilMoistureMin: String
) {
    companion object {
        fun DomainPlant.toPlant() = Plant(
            uuid = uuid,
            name = name,
            photoUri = photoUri,
            airHumidityConfig = AirHumidityConfig(
                min = airHumidityMin,
                max = airHumidityMax,
            ),
            lightLuxConfig = LightLuxConfig(
                min = lightLuxMin,
                max = lightLuxMax,
            ),
            soilMoistureMin = soilMoistureMin?.formatAsPercents(0) ?: "＿",
            airTempConfig = AirTempConfig(
                min = airTempMin,
                max = airTempMax,
            )
        )
    }
}
