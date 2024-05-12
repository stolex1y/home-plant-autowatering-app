package ru.filimonov.hpa.domain.model.readings

data class DomainSensorReadings(
    val soilMoisture: DomainSensorReading<Float>?,
    val airHumidity: DomainSensorReading<Float>?,
    val airTemp: DomainSensorReading<Float>?,
    val lightLevel: DomainSensorReading<Int>?,
    val batteryCharge: DomainSensorReading<Float>?,
)
