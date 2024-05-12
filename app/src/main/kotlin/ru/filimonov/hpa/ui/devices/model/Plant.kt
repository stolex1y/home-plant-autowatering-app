package ru.filimonov.hpa.ui.devices.model

import ru.filimonov.hpa.domain.model.DomainPlant
import java.util.UUID

data class Plant(
    val uuid: UUID,
    val name: String,
) {
    companion object {
        fun DomainPlant.toPlant() = Plant(
            uuid = uuid,
            name = name,
        )
    }
}
