package ru.filimonov.hpa.data.remote.model.plant

import ru.filimonov.hpa.domain.model.Plant
import java.util.UUID

data class UpdatePlantRequest(
    val uuid: UUID,
    val name: String,
)

fun Plant.toUpdatePlantRequest() = UpdatePlantRequest(
    uuid = uuid,
    name = name,
)
