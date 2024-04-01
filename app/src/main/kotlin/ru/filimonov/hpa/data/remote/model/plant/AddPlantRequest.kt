package ru.filimonov.hpa.data.remote.model.plant

import ru.filimonov.hpa.domain.model.Plant

data class AddPlantRequest(
    val name: String,
)

fun Plant.toAddPlantRequest() = AddPlantRequest(
    name = name
)
