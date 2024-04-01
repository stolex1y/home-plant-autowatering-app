package ru.filimonov.hpa.ui.device.adding.model

import ru.filimonov.hpa.domain.model.ExtendedDevice
import java.util.Calendar
import java.util.UUID

data class AddingDevice(
    val mac: String
) {
    fun toExtendedDevice() = ExtendedDevice(
        uuid = UUID.randomUUID(),
        mac = mac,
        plantId = null,
        createdDate = Calendar.getInstance()
    )
}

/*@Stable
class AddingDevice(
    mac: String = "",
) : ValidatedEntity() {
    val mac: ValidatedProperty<String> = addValidatedProperty(
        mac,
        Conditions.MacAddress
    )

    fun toExtendedDevice() = ExtendedDevice(
        uuid = UUID.randomUUID(),
        mac = mac.value,
        plantId = null,
        createdDate = Calendar.getInstance()
    )

    fun saver(): Saver<AddingDevice, Any> {
        return getSaver<AddingDevice> { AddingDevice() }
    }
}*/
