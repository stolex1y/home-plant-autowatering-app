package ru.filimonov.hpa.data.service.readings

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import ru.filimonov.hpa.common.coroutine.CoroutineNames
import ru.filimonov.hpa.common.coroutine.FlowExtensions
import ru.filimonov.hpa.data.remote.mapLatestResultExceptionToDomain
import ru.filimonov.hpa.data.remote.repository.readings.SensorReadingsRemoteRepository
import ru.filimonov.hpa.domain.service.readings.SensorReadingsService
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Named

class SensorReadingsServiceImpl @Inject constructor(
    private val sensorReadingsRemoteRepository: SensorReadingsRemoteRepository,
    @Named(CoroutineNames.IO_DISPATCHER) private val dispatcher: CoroutineDispatcher,
) : SensorReadingsService {
    override fun getAllLast(deviceId: UUID) = FlowExtensions.makeSyncFlowCatching {
        sensorReadingsRemoteRepository.getAllLast(deviceId).toDomain()
    }
        .onStart { Timber.d("start getting all last sensor readings") }
        .distinctUntilChanged()
        .onEach { Timber.v("get new all last sensor readings") }
        .mapLatestResultExceptionToDomain()
        .flowOn(dispatcher)
}
