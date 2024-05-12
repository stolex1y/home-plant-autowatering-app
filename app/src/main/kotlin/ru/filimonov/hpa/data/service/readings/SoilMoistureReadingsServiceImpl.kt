package ru.filimonov.hpa.data.service.readings

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import ru.filimonov.hpa.common.coroutine.CoroutineNames
import ru.filimonov.hpa.common.coroutine.FlowExtensions
import ru.filimonov.hpa.common.utils.time.DateUtils.toEpochMillis
import ru.filimonov.hpa.common.utils.time.DateUtils.toShortFormatString
import ru.filimonov.hpa.data.remote.mapLatestResultExceptionToDomain
import ru.filimonov.hpa.data.remote.repository.readings.SoilMoistureReadingsRemoteRepository
import ru.filimonov.hpa.domain.service.readings.SoilMoistureReadingsService
import timber.log.Timber
import java.time.ZonedDateTime
import java.util.UUID
import javax.inject.Inject
import javax.inject.Named

class SoilMoistureReadingsServiceImpl @Inject constructor(
    private val soilMoistureReadingsRemoteRepository: SoilMoistureReadingsRemoteRepository,
    @Named(CoroutineNames.IO_DISPATCHER) private val dispatcher: CoroutineDispatcher,
) : SoilMoistureReadingsService {
    override fun getLast(deviceId: UUID) = FlowExtensions.makeSyncFlowCatching {
        soilMoistureReadingsRemoteRepository.getLast(deviceId).toDomain()
    }
        .onStart { Timber.d("start getting last soil moisture reading") }
        .distinctUntilChanged()
        .onEach { Timber.v("get new last soil moisture reading") }
        .mapLatestResultExceptionToDomain()
        .flowOn(dispatcher)

    override fun getForPeriodByHour(
        deviceId: UUID,
        fromTimestamp: ZonedDateTime,
        toTimestamp: ZonedDateTime,
    ) = FlowExtensions.makeSyncFlowCatching {
        soilMoistureReadingsRemoteRepository.getForPeriodByHour(
            deviceId,
            fromTimestamp.toEpochMillis(),
            toTimestamp.toEpochMillis()
        ).map {
            it.toDomain()
        }
    }
        .onStart { Timber.d("start getting soil moisture readings for period (from ${fromTimestamp.toShortFormatString()} to ${toTimestamp.toShortFormatString()}) by hour") }
        .distinctUntilChanged()
        .onEach { Timber.v("get new  soil moisture readings for period by hour") }
        .mapLatestResultExceptionToDomain()
        .flowOn(dispatcher)
}
