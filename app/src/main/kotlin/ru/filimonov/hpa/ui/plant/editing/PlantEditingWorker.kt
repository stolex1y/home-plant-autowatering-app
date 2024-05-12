package ru.filimonov.hpa.ui.plant.editing

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ru.filimonov.hpa.R
import ru.filimonov.hpa.domain.model.DomainPlant
import ru.filimonov.hpa.domain.service.PlantService
import ru.filimonov.hpa.ui.common.notification.NotificationUtils
import ru.filimonov.hpa.ui.common.work.AbstractWorker

@HiltWorker
class PlantEditingWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val plantService: PlantService,
) : AbstractWorker<DomainPlant, Unit>(
    NOTIFICATION_ID,
    appContext.getString(R.string.plant_editing),
    WORK_NAME,
    DomainPlant::class,
    appContext,
    workerParams
) {
    override suspend fun action(input: DomainPlant): kotlin.Result<Unit> {
        return plantService.update(plant = input)
    }

    companion object {
        val WORK_NAME = "plant_editing"
        val NOTIFICATION_ID = NotificationUtils.getUniqueNotificationId()

        fun createWorkRequest(plant: DomainPlant): OneTimeWorkRequest {
            return createWorkRequest<PlantEditingWorker, DomainPlant, Unit>(
                input = plant,
                tag = WORK_NAME,
            )
        }
    }
}
