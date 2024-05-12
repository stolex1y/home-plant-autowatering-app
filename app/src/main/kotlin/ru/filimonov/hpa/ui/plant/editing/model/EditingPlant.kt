package ru.filimonov.hpa.ui.plant.editing.model

import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.mapSaver
import ru.filimonov.hpa.R
import ru.filimonov.hpa.domain.model.DomainPlant
import ru.filimonov.hpa.ui.common.validation.Conditions
import ru.filimonov.hpa.ui.common.validation.ValidatedEntity
import ru.filimonov.hpa.ui.common.validation.ValidatedProperty
import java.net.URI
import java.util.UUID

@Stable
class EditingPlant(
    val uuid: UUID,
    name: String,
    photoUri: URI?,
    airTempMin: String,
    airTempMax: String,
    airHumidityMin: String,
    airHumidityMax: String,
    soilMoistureMin: String,
    lightLuxMax: String,
    lightLuxMin: String,
) : ValidatedEntity() {
    companion object {
        fun saver() = mapSaver(
            save = { entity ->
                mapOf(
                    "id" to entity.uuid,
                    "name" to entity.name.value,
                    "photoUri" to entity.photoUri.value,
                    "airTempMin" to entity.airTempMin.value,
                    "airTempMax" to entity.airTempMax.value,
                    "airHumidityMin" to entity.airHumidityMin.value,
                    "airHumidityMax" to entity.airHumidityMax.value,
                    "soilMoistureMin" to entity.soilMoistureMin.value,
                    "lightLuxMin" to entity.lightLuxMin.value,
                    "lightLuxMax" to entity.lightLuxMax.value,
                )
            },
            restore = { restored ->
                EditingPlant(
                    name = restored["name"] as String,
                    photoUri = restored["photoUri"] as URI?,
                    uuid = restored["id"] as UUID,
                    airTempMin = restored["airTempMin"] as String,
                    airTempMax = restored["airTempMax"] as String,
                    airHumidityMin = restored["airHumidityMin"] as String,
                    airHumidityMax = restored["airHumidityMax"] as String,
                    lightLuxMin = restored["lightLuxMin"] as String,
                    lightLuxMax = restored["lightLuxMax"] as String,
                    soilMoistureMin = restored["soilMoistureMin"] as String,
                )
            }
        )
    }

    val name: ValidatedProperty<String> =
        addValidatedProperty(name, Conditions.RequiredField(R.string.required_field))
    val photoUri: ValidatedProperty<URI?> = addValidatedProperty(
        initialValue = photoUri, condition = Conditions.None()
    )
    val airTempMax: ValidatedProperty<String> =
        addValidatedProperty(
            initialValue = airTempMax,
            condition = Conditions.FloatInRange(
                range = 20f..40f,
                errorStringRes = R.string.number_out_of_range_error
            )
        )
    val airTempMin: ValidatedProperty<String> =
        addValidatedProperty(
            initialValue = airTempMin,
            condition = Conditions.FloatInRange(
                range = 0f..40f,
                errorStringRes = R.string.number_out_of_range_error
            )
        )
    val airHumidityMin: ValidatedProperty<String> =
        addValidatedProperty(
            initialValue = airHumidityMin,
            condition = Conditions.FloatInRange(
                range = 0f..100f,
                errorStringRes = R.string.number_out_of_range_error
            )
        )
    val airHumidityMax: ValidatedProperty<String> =
        addValidatedProperty(
            initialValue = airHumidityMax,
            condition = Conditions.FloatInRange(
                range = 0f..100f,
                errorStringRes = R.string.number_out_of_range_error
            )
        )
    val soilMoistureMin: ValidatedProperty<String> =
        addValidatedProperty(
            initialValue = soilMoistureMin,
            condition = Conditions.FloatInRange(
                range = 0f..100f,
                errorStringRes = R.string.number_out_of_range_error
            )
        )
    val lightLuxMin: ValidatedProperty<String> =
        addValidatedProperty(
            initialValue = lightLuxMin,
            condition = Conditions.IntInRange(
                range = 0..130_000,
                errorStringRes = R.string.integer_number_out_of_range_error
            )
        )
    val lightLuxMax: ValidatedProperty<String> =
        addValidatedProperty(
            initialValue = lightLuxMax,
            condition = Conditions.IntInRange(
                range = 0..130_000,
                R.string.integer_number_out_of_range_error
            )
        )

    fun toDomain(): DomainPlant {
        return DomainPlant(
            uuid = uuid,
            name = name.value,
            photoUri = photoUri.value,
            airTempMin = airTempMin.value.toFloat(),
            airTempMax = airTempMax.value.toFloat(),
            airHumidityMin = airHumidityMin.value.toFloat(),
            airHumidityMax = airHumidityMax.value.toFloat(),
            lightLuxMin = lightLuxMin.value.toInt(),
            lightLuxMax = lightLuxMax.value.toInt(),
            soilMoistureMin = soilMoistureMin.value.toFloat(),
        )
    }
}
