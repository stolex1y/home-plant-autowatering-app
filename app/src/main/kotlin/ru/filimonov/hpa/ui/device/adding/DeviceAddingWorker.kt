package ru.filimonov.hpa.ui.device.adding

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ru.filimonov.hpa.R
import ru.filimonov.hpa.domain.model.ExtendedDevice
import ru.filimonov.hpa.domain.service.device.DeviceService
import ru.filimonov.hpa.ui.common.notification.NotificationUtils
import ru.filimonov.hpa.ui.common.work.AbstractWorker
import java.util.UUID

@HiltWorker
class DeviceAddingWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val deviceService: DeviceService,
) : AbstractWorker<ExtendedDevice, UUID>(
    NOTIFICATION_ID,
    appContext.getString(R.string.device_adding),
    WORK_NAME,
    ExtendedDevice::class,
    appContext,
    workerParams
) {
    override suspend fun action(input: ExtendedDevice): kotlin.Result<UUID> {
        return deviceService.add(input).map { it.uuid }
    }

    companion object {
        val WORK_NAME = "device_adding"
        val NOTIFICATION_ID = NotificationUtils.getUniqueNotificationId()

        fun createWorkRequest(entity: ExtendedDevice): OneTimeWorkRequest {
            return createWorkRequest<DeviceAddingWorker, ExtendedDevice, UUID>(entity, WORK_NAME)
        }
    }
}
