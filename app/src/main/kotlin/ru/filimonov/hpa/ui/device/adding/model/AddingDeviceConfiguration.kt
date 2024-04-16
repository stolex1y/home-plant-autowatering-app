package ru.filimonov.hpa.ui.device.adding.model

import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.mapSaver
import ru.filimonov.hpa.R
import ru.filimonov.hpa.domain.model.DeviceConfiguration
import ru.filimonov.hpa.ui.common.validation.Conditions
import ru.filimonov.hpa.ui.common.validation.ValidatedEntity
import ru.filimonov.hpa.ui.common.validation.ValidatedProperty
import java.net.URL
import java.util.UUID

@Stable
class AddingDeviceConfiguration(
    ssid: String = "",
    pass: String = "",
) : ValidatedEntity() {
    val ssid: ValidatedProperty<String> = addValidatedProperty(
        initialValue = ssid, Conditions.RequiredField(
            R.string.required_field
        )
    )
    val pass: ValidatedProperty<String> = addValidatedProperty(
        initialValue = pass, Conditions.RequiredField(
            R.string.required_field
        )
    )

    fun toDomain(deviceId: UUID, serverUrl: URL) = DeviceConfiguration(
        ssid = ssid.value,
        pass = pass.value,
        deviceId = deviceId,
        serverUrl = serverUrl,
    )

    companion object {
        fun saver() = mapSaver(
            save = { entity ->
                mapOf("ssid" to entity.ssid.value, "pass" to entity.pass.value)
            },
            restore = { restored ->
                AddingDeviceConfiguration(
                    ssid = restored["ssid"] as String,
                    pass = restored["pass"] as String,
                )
            }
        )
    }
}
