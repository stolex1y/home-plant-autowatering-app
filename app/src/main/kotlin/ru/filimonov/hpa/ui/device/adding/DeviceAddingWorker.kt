package ru.filimonov.hpa.ui.device.adding

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ru.filimonov.hpa.BuildConfig
import ru.filimonov.hpa.R
import ru.filimonov.hpa.domain.errors.BadRequestException
import ru.filimonov.hpa.domain.model.device.DomainDevice
import ru.filimonov.hpa.domain.model.device.DomainDeviceConfiguration
import ru.filimonov.hpa.domain.service.device.DeviceConfiguringService
import ru.filimonov.hpa.domain.service.device.DeviceService
import ru.filimonov.hpa.ui.common.notification.NotificationUtils
import ru.filimonov.hpa.ui.common.work.AbstractWorker
import ru.filimonov.hpa.ui.common.work.WorkErrorWrapper
import ru.filimonov.hpa.ui.device.adding.model.AddingDevice
import ru.filimonov.hpa.ui.device.adding.model.AddingErrors
import java.net.URL
import java.util.UUID

@HiltWorker
class DeviceAddingWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val deviceService: DeviceService,
    private val deviceConfiguringService: DeviceConfiguringService,
) : AbstractWorker<AddingDevice, UUID>(
    NOTIFICATION_ID,
    appContext.getString(R.string.device_adding),
    WORK_NAME,
    AddingDevice::class,
    appContext,
    workerParams
) {
    override suspend fun action(input: AddingDevice): kotlin.Result<UUID> {
        deviceService.add(
            DomainDevice(
                mac = input.mac,
                name = input.name,
            )
        ).onSuccess { addedDevice ->
            deviceConfiguringService.sendConfiguration(
                DomainDeviceConfiguration(
                    ssid = input.ssid,
                    pass = input.pass,
                    deviceId = addedDevice.uuid,
                    serverUrl = URL(BuildConfig.DEVICE_BASE_URL)
                )
            ).onSuccess {
                return kotlin.Result.success(addedDevice.uuid)
            }.onFailure {
                deviceService.delete(addedDevice.uuid)
                return kotlin.Result.failure(WorkErrorWrapper(AddingErrors.InvalidDeviceConfigurationError))
            }
        }.onFailure {
            return if (it is BadRequestException) {
                kotlin.Result.failure(WorkErrorWrapper(AddingErrors.DeviceAlreadyAddedError))
            } else {
                kotlin.Result.failure(it)
            }
        }
        return kotlin.Result.failure(IllegalStateException())
    }

    companion object {
        val WORK_NAME = "device_adding"
        val NOTIFICATION_ID = NotificationUtils.getUniqueNotificationId()

        fun createWorkRequest(entity: AddingDevice): OneTimeWorkRequest {
            return createWorkRequest<DeviceAddingWorker, AddingDevice, UUID>(
                entity,
                WORK_NAME
            )
        }
    }
}
