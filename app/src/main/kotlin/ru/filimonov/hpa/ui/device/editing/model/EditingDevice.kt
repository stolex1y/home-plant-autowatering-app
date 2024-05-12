package ru.filimonov.hpa.ui.device.editing.model

import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.mapSaver
import ru.filimonov.hpa.R
import ru.filimonov.hpa.domain.model.device.DomainDevice
import ru.filimonov.hpa.ui.common.validation.Conditions
import ru.filimonov.hpa.ui.common.validation.ValidatedEntity
import ru.filimonov.hpa.ui.common.validation.ValidatedProperty
import java.net.URI
import java.util.UUID

@Stable
class EditingDevice(
    val uuid: UUID,
    name: String = "",
    plant: Plant? = null,
    photoUri: URI? = null,
) : ValidatedEntity() {
    val name: ValidatedProperty<String> = addValidatedProperty(
        initialValue = name, Conditions.RequiredField(
            R.string.required_field
        )
    )
    val plant: ValidatedProperty<Plant?> = addValidatedProperty(
        initialValue = plant, Conditions.None()
    )
    val photoUri: ValidatedProperty<URI?> = addValidatedProperty(
        initialValue = photoUri, Conditions.None()
    )

    fun toDomain() = DomainDevice(
        plantId = plant.value?.uuid,
        name = name.value,
        photoUri = photoUri.value,
        mac = "",
        uuid = uuid,
    )

    companion object {
        fun Device.toEditingDevice() = EditingDevice(
            uuid = uuid,
            name = name,
            plant = plant,
            photoUri = photoUri,
        )

        fun saver() = mapSaver(
            save = { entity ->
                mapOf(
                    "plant" to entity.plant.value,
                    "id" to entity.uuid,
                    "name" to entity.name.value,
                    "photoUri" to entity.photoUri.value,
                )
            },
            restore = { restored ->
                EditingDevice(
                    plant = restored["plant"] as Plant?,
                    name = restored["name"] as String,
                    photoUri = restored["photoUri"] as URI?,
                    uuid = restored["id"] as UUID,
                )
            }
        )
    }
}
