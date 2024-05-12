package ru.filimonov.hpa.ui.device.editing.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.filimonov.hpa.domain.model.DomainPlant
import java.util.UUID

@Parcelize
data class Plant(
    val uuid: UUID,
    val name: String,
) : Parcelable {
    companion object {
        fun DomainPlant.toPlant() = Plant(
            uuid = uuid,
            name = name,
        )
    }
}
