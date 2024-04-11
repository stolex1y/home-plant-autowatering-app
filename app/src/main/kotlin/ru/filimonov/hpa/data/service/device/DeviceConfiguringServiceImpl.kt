package ru.filimonov.hpa.data.service.device

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import ru.filimonov.hpa.common.coroutine.CoroutineNames
import ru.filimonov.hpa.common.coroutine.FlowExtensions.makeSyncFlow
import ru.filimonov.hpa.data.remote.repository.DeviceConfigurationRemoteRepository
import ru.filimonov.hpa.domain.model.DeviceConfiguration
import ru.filimonov.hpa.domain.model.DeviceInfo
import ru.filimonov.hpa.domain.service.device.DeviceConfiguringService
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import kotlin.time.Duration.Companion.seconds

class DeviceConfiguringServiceImpl @Inject constructor(
    private val remoteRepository: DeviceConfigurationRemoteRepository,
    @Named(CoroutineNames.IO_DISPATCHER) private val dispatcher: CoroutineDispatcher,
) : DeviceConfiguringService {
    override fun getDeviceInfo(): Flow<Result<DeviceInfo>> =
        makeSyncFlow(retryDelay = 10.seconds, syncDelay = Int.MAX_VALUE.seconds) {
            remoteRepository.getInfo()
        }.onStart {
            Timber.d("get device info")
        }.flowOn(dispatcher)

    override suspend fun sendConfiguration(deviceConfiguration: DeviceConfiguration): Result<Unit> =
        kotlin.runCatching {
            withContext(dispatcher) {
                Timber.d("send device configuration")
                remoteRepository.updateConfiguration(deviceConfiguration)
            }
        }
}