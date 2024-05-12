package ru.filimonov.hpa.ui.device.editing

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ru.filimonov.hpa.R
import ru.filimonov.hpa.domain.service.device.DeviceService
import ru.filimonov.hpa.ui.common.notification.NotificationUtils
import ru.filimonov.hpa.ui.common.work.AbstractWorker
import java.util.UUID

@HiltWorker
class DeviceDeletingWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val deviceService: DeviceService,
) : AbstractWorker<UUID, Unit>(
    NOTIFICATION_ID,
    appContext.getString(R.string.device_deleting),
    WORK_NAME,
    UUID::class,
    appContext,
    workerParams
) {
    override suspend fun action(input: UUID): kotlin.Result<Unit> {
        return deviceService.delete(uuid = input)
    }

    companion object {
        val WORK_NAME = "device_deleting"
        val NOTIFICATION_ID = NotificationUtils.getUniqueNotificationId()

        fun createWorkRequest(deviceId: UUID): OneTimeWorkRequest {
            return createWorkRequest<DeviceDeletingWorker, UUID, Unit>(
                input = deviceId,
                tag = WORK_NAME,
            )
        }
    }
}
