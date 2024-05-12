package ru.filimonov.hpa.ui.devices.model

import ru.filimonov.hpa.domain.model.DomainPlant
import ru.filimonov.hpa.domain.model.device.DomainDevice
import ru.filimonov.hpa.ui.devices.model.DeviceWithPlant.Companion.toDeviceWithPlant
import ru.filimonov.hpa.ui.devices.model.DeviceWithoutPlant.Companion.toDeviceWithoutPlant
import ru.filimonov.hpa.ui.devices.model.Plant.Companion.toPlant
import java.net.URI
import java.util.UUID

sealed class Device(
    val uuid: UUID,
    val name: String,
    val photoUri: URI?,
) {
    companion object {
        fun DomainDevice.toDevice(domainPlant: DomainPlant? = null): Device {
            return if (domainPlant == null) {
                this.toDeviceWithoutPlant()
            } else {
                this.toDeviceWithPlant(domainPlant)
            }
        }
    }
}

class DeviceWithPlant(
    uuid: UUID,
    name: String,
    photoUri: URI?,
    val plant: Plant,
) : Device(uuid = uuid, name = name, photoUri = photoUri) {
    companion object {
        fun DomainDevice.toDeviceWithPlant(domainPlant: DomainPlant) = DeviceWithPlant(
            uuid = uuid,
            name = name,
            photoUri = photoUri,
            plant = domainPlant.toPlant(),
        )
    }
}

class DeviceWithoutPlant(
    uuid: UUID,
    name: String,
    photoUri: URI?,
) : Device(uuid = uuid, name = name, photoUri = photoUri) {
    companion object {
        fun DomainDevice.toDeviceWithoutPlant() = DeviceWithoutPlant(
            uuid = uuid,
            name = name,
            photoUri = photoUri,
        )
    }
}
