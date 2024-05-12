package ru.filimonov.hpa.data.service.device

import android.content.Context
import android.graphics.Bitmap
import coil.ImageLoader
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import ru.filimonov.hpa.common.coroutine.CoroutineNames
import ru.filimonov.hpa.common.coroutine.FlowExtensions.makeSyncFlowCatching
import ru.filimonov.hpa.common.utils.isLocalResource
import ru.filimonov.hpa.data.remote.mapExceptionToDomain
import ru.filimonov.hpa.data.remote.mapLatestResultExceptionToDomain
import ru.filimonov.hpa.data.remote.model.device.DeviceResponse
import ru.filimonov.hpa.data.remote.model.device.toAddDeviceRequest
import ru.filimonov.hpa.data.remote.model.device.toUpdateDeviceRequest
import ru.filimonov.hpa.data.remote.repository.DeviceRemoteRepository
import ru.filimonov.hpa.data.service.PhotoServiceUtils
import ru.filimonov.hpa.domain.model.device.DomainDevice
import ru.filimonov.hpa.domain.service.device.DeviceService
import timber.log.Timber
import java.net.URI
import java.util.UUID
import javax.inject.Inject
import javax.inject.Named

class DeviceServiceImpl @Inject constructor(
    private val deviceRemoteRepository: DeviceRemoteRepository,
    private val imageLoader: ImageLoader,
    @ApplicationContext private val applicationContext: Context,
    @Named(CoroutineNames.IO_DISPATCHER) private val dispatcher: CoroutineDispatcher,
) : DeviceService {
    override fun getAll(): Flow<Result<List<DomainDevice>>> = makeSyncFlowCatching {
        deviceRemoteRepository.getAll().map(DeviceResponse::toDomain)
    }
        .onStart { Timber.d("start getting all devices") }
        .distinctUntilChanged()
        .onEach { Timber.v("get new all devices") }
        .mapLatestResultExceptionToDomain()
        .flowOn(dispatcher)

    override fun get(deviceId: UUID): Flow<Result<DomainDevice>> = makeSyncFlowCatching {
        deviceRemoteRepository.get(deviceId).toDomain()
    }
        .onStart { Timber.d("start getting device with id: $deviceId") }
        .distinctUntilChanged()
        .onEach { Timber.v("get new device with id: $deviceId") }
        .mapLatestResultExceptionToDomain()
        .flowOn(dispatcher)

    override suspend fun add(device: DomainDevice): Result<DomainDevice> = runCatching {
        withContext(dispatcher) {
            Timber.d("add new device")
            val addedDevice = deviceRemoteRepository.add(device.toAddDeviceRequest()).toDomain()
            Timber.d("successfully added")
            addedDevice
        }
    }.mapExceptionToDomain()

    override suspend fun delete(uuid: UUID): Result<Unit> = runCatching {
        withContext(dispatcher) {
            Timber.d("add new device")
            deviceRemoteRepository.delete(uuid)
            Timber.d("successfully added")
        }
    }.mapExceptionToDomain()

    override suspend fun update(device: DomainDevice): Result<Unit> = runCatching {
        withContext(dispatcher) {
            Timber.d("update device with id - ${device.uuid}")
            if (device.photoUri?.isLocalResource() == true) {
                Timber.d("trying update device photo")
                val multipartBodyPart = PhotoServiceUtils.photoToMultipartBodyPart(
                    photoUri = device.photoUri,
                    applicationContext = applicationContext,
                    coroutineDispatcher = dispatcher,
                )
                    ?: throw IllegalStateException("couldn't get new device photo by uri: ${device.photoUri}")
                deviceRemoteRepository.updatePhoto(
                    deviceId = device.uuid,
                    photo = multipartBodyPart,
                )
                Timber.d("successfully updated device photo")
            }
            deviceRemoteRepository.update(device.uuid, device.toUpdateDeviceRequest()).toDomain()
            Timber.d("successfully updated")
        }
    }.mapExceptionToDomain()

    override fun getPhoto(photoUri: URI): Flow<Result<Bitmap?>> {
        return PhotoServiceUtils.loadPhoto(
            photoUri = photoUri,
            imageLoader = imageLoader,
            applicationContext = applicationContext,
            dispatcher = dispatcher,
        )
    }
}
