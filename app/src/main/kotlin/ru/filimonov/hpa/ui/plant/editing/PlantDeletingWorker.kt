package ru.filimonov.hpa.ui.plant.editing

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ru.filimonov.hpa.R
import ru.filimonov.hpa.domain.service.PlantService
import ru.filimonov.hpa.ui.common.notification.NotificationUtils
import ru.filimonov.hpa.ui.common.work.AbstractWorker
import java.util.UUID

@HiltWorker
class PlantDeletingWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val plantService: PlantService,
) : AbstractWorker<UUID, Unit>(
    NOTIFICATION_ID,
    appContext.getString(R.string.plant_deleting),
    WORK_NAME,
    UUID::class,
    appContext,
    workerParams
) {
    override suspend fun action(input: UUID): kotlin.Result<Unit> {
        return plantService.delete(uuid = input)
    }

    companion object {
        val WORK_NAME = "plant_deleting"
        val NOTIFICATION_ID = NotificationUtils.getUniqueNotificationId()

        fun createWorkRequest(plantId: UUID): OneTimeWorkRequest {
            return createWorkRequest<PlantDeletingWorker, UUID, Unit>(
                input = plantId,
                tag = WORK_NAME,
            )
        }
    }
}
