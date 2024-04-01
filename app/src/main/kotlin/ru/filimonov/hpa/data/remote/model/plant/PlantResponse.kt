package ru.filimonov.hpa.data.remote.model.plant

import ru.filimonov.hpa.domain.model.Plant
import java.util.UUID

data class PlantResponse(
    val uuid: UUID,
    val name: String,
) {
    fun toDomain() = Plant(
        uuid = uuid,
        name = name,
    )
}
