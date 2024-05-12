package ru.filimonov.hpa.ui.device.details.model

import ru.filimonov.hpa.domain.model.DomainPlant
import ru.filimonov.hpa.domain.model.device.DomainDevice
import ru.filimonov.hpa.domain.model.readings.DomainSensorReadings
import ru.filimonov.hpa.ui.device.details.model.Plant.Companion.toPlant
import ru.filimonov.hpa.ui.device.details.model.SensorReadings.Companion.toSensorReadings
import java.net.URI
import java.util.UUID

data class Device(
    val uuid: UUID,
    val plant: Plant?,
    val name: String,
    val photoUri: URI?,
    val sensorReadings: SensorReadings,
) {
    companion object {
        fun DomainDevice.toDevice(
            domainPlant: DomainPlant? = null,
            domainSensorReadings: DomainSensorReadings
        ) = Device(
            uuid = uuid,
            plant = domainPlant?.toPlant(),
            name = name,
            photoUri = photoUri,
            sensorReadings = domainSensorReadings.toSensorReadings(),
        )
    }
}
