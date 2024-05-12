package ru.filimonov.hpa.ui.device.adding

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeout
import ru.filimonov.hpa.BuildConfig
import ru.filimonov.hpa.R
import ru.filimonov.hpa.common.coroutine.FlowExtensions.filterResult
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
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

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
        try {
            val addedDevice = deviceService.add(
                DomainDevice(
                    mac = input.mac,
                    name = input.name,
                )
            ).getOrThrow()
            try {
                val config = DomainDeviceConfiguration(
                    wifiSsid = input.ssid,
                    wifiPass = input.pass,
                    deviceId = addedDevice.uuid,
                    mqttUrl = BuildConfig.MQTT_URL,
                    mqttUsername = BuildConfig.MQTT_DEVICE_USERNAME,
                    mqttPassword = BuildConfig.MQTT_DEVICE_PASSWORD,
                )
                sendConfigAndSwitchMode(config = config).getOrThrow()
                return kotlin.Result.success(addedDevice.uuid)
            } catch (t: Throwable) {
                deviceService.delete(addedDevice.uuid)
                return kotlin.Result.failure(t)
            }
        } catch (t: Throwable) {
            return if (t is BadRequestException) {
                kotlin.Result.failure(WorkErrorWrapper(AddingErrors.DeviceAlreadyAddedError))
            } else {
                kotlin.Result.failure(t)
            }
        }
    }

    private suspend fun sendConfigAndSwitchMode(config: DomainDeviceConfiguration): kotlin.Result<Unit> {
        return kotlin.runCatching {
            deviceConfiguringService.sendConfiguration(deviceConfiguration = config).getOrThrow()
            try {
                withTimeout(timeout = 30.seconds) {
                    deviceConfiguringService.isConnected()
                        .filterResult { it }
                        .first()
                        .getOrThrow()
                }
            } catch (e: TimeoutCancellationException) {
                throw WorkErrorWrapper(AddingErrors.InvalidDeviceConfigurationError)
            }
        }
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
