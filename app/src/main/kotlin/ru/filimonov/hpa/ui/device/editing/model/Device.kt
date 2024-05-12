package ru.filimonov.hpa.ui.device.editing.model

import ru.filimonov.hpa.domain.model.DomainPlant
import ru.filimonov.hpa.domain.model.device.DomainDevice
import ru.filimonov.hpa.ui.device.editing.model.Plant.Companion.toPlant
import java.net.URI
import java.util.UUID

data class Device(
    val uuid: UUID,
    val plant: Plant?,
    val name: String,
    val photoUri: URI?,
) {
    companion object {
        fun DomainDevice.toDevice(plant: DomainPlant? = null) = Device(
            uuid = uuid,
            plant = plant?.toPlant(),
            name = name,
            photoUri = photoUri,
        )
    }
}
