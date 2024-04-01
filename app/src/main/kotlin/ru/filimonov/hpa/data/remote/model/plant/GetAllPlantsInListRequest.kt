package ru.filimonov.hpa.data.remote.model.plant

import java.util.UUID

data class GetAllPlantsInListRequest(
    val ids: List<UUID>
)
