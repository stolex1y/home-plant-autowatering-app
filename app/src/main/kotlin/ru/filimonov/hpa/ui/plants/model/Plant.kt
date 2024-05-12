package ru.filimonov.hpa.ui.plants.model

import ru.filimonov.hpa.domain.model.DomainPlant
import java.net.URI
import java.util.UUID

data class Plant(
    val uuid: UUID,
    val name: String,
    val photoUri: URI?,
) {
    companion object {
        fun DomainPlant.toPlant() = Plant(
            uuid = uuid,
            name = name,
            photoUri = photoUri,
        )
    }
}
