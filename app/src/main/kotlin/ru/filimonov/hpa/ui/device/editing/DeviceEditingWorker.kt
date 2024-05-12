package ru.filimonov.hpa.ui.device.editing

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ru.filimonov.hpa.R
import ru.filimonov.hpa.domain.model.device.DomainDevice
import ru.filimonov.hpa.domain.service.device.DeviceService
import ru.filimonov.hpa.ui.common.notification.NotificationUtils
import ru.filimonov.hpa.ui.common.work.AbstractWorker

@HiltWorker
class DeviceEditingWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val deviceService: DeviceService,
) : AbstractWorker<DomainDevice, Unit>(
    NOTIFICATION_ID,
    appContext.getString(R.string.device_editing),
    WORK_NAME,
    DomainDevice::class,
    appContext,
    workerParams
) {
    override suspend fun action(input: DomainDevice): kotlin.Result<Unit> {
        return deviceService.update(device = input)
    }

    companion object {
        val WORK_NAME = "device_editing"
        val NOTIFICATION_ID = NotificationUtils.getUniqueNotificationId()

        fun createWorkRequest(device: DomainDevice): OneTimeWorkRequest {
            return createWorkRequest<DeviceEditingWorker, DomainDevice, Unit>(
                input = device,
                tag = WORK_NAME,
            )
        }
    }
}
