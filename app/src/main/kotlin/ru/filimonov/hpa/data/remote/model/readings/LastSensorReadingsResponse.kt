package ru.filimonov.hpa.data.remote.model.readings

import ru.filimonov.hpa.domain.model.readings.DomainSensorReadings

data class LastSensorReadingsResponse(
    val soilMoisture: SensorReadingResponse<Float>?,
    val airHumidity: SensorReadingResponse<Float>?,
    val airTemp: SensorReadingResponse<Float>?,
    val lightLevel: SensorReadingResponse<Int>?,
    val batteryCharge: SensorReadingResponse<Float>?,
) {
    fun toDomain() = DomainSensorReadings(
        soilMoisture = soilMoisture?.toDomain(),
        airHumidity = airHumidity?.toDomain(),
        airTemp = airTemp?.toDomain(),
        lightLevel = lightLevel?.toDomain(),
        batteryCharge = batteryCharge?.toDomain(),
    )
}
