package ru.filimonov.hpa.data.service.device

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import ru.filimonov.hpa.common.coroutine.CoroutineNames
import ru.filimonov.hpa.common.coroutine.FlowExtensions.makeSyncFlowCatching
import ru.filimonov.hpa.data.remote.mapExceptionToDomain
import ru.filimonov.hpa.data.remote.mapLatestResultExceptionToDomain
import ru.filimonov.hpa.data.remote.repository.DeviceConfigurationRemoteRepository
import ru.filimonov.hpa.domain.model.device.DomainDeviceConfiguration
import ru.filimonov.hpa.domain.model.device.DomainDeviceInfo
import ru.filimonov.hpa.domain.service.device.DeviceConfiguringService
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import kotlin.time.Duration.Companion.seconds

class DeviceConfiguringServiceImpl @Inject constructor(
    private val remoteRepository: DeviceConfigurationRemoteRepository,
    @Named(CoroutineNames.IO_DISPATCHER) private val dispatcher: CoroutineDispatcher,
) : DeviceConfiguringService {
    override fun getDeviceInfo(): Flow<Result<DomainDeviceInfo>> =
        makeSyncFlowCatching(retryDelay = 10.seconds, syncDelay = Int.MAX_VALUE.seconds) {
            remoteRepository.getInfo()
        }
            .mapLatestResultExceptionToDomain()
            .onStart {
                Timber.d("get device info")
            }
            .flowOn(dispatcher)

    override suspend fun sendConfiguration(deviceConfiguration: DomainDeviceConfiguration): Result<Unit> =
        kotlin.runCatching {
            withContext(dispatcher) {
                Timber.d("send device configuration")
                remoteRepository.updateConfiguration(deviceConfiguration)
            }
        }.mapExceptionToDomain()
}
