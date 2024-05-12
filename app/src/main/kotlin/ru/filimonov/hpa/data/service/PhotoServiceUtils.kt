package ru.filimonov.hpa.data.service

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.network.HttpException
import coil.request.ErrorResult
import coil.request.ImageRequest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.filimonov.hpa.common.coroutine.FlowExtensions.makeSyncFlowOpt
import ru.filimonov.hpa.data.remote.isNotModified
import ru.filimonov.hpa.data.remote.mapLatestResultExceptionToDomain
import ru.filimonov.hpa.ui.common.utils.compressImage
import timber.log.Timber
import java.net.URI
import java.util.Optional

object PhotoServiceUtils {
    fun loadPhoto(
        photoUri: URI,
        imageLoader: ImageLoader,
        applicationContext: Context,
        dispatcher: CoroutineDispatcher,
    ): Flow<Result<Bitmap?>> {
        return makeSyncFlowOpt {
            val result = imageLoader.execute(
                ImageRequest.Builder(applicationContext)
                    .data(photoUri.toString())
                    .build()
            )
            if (result is ErrorResult) {
                val throwable = result.throwable
                if (throwable is HttpException && throwable.isNotModified()) {
                    return@makeSyncFlowOpt Optional.empty()
                }
                return@makeSyncFlowOpt Optional.of(Result.failure(result.throwable))
            }
            return@makeSyncFlowOpt Optional.of(Result.success(result.drawable?.toBitmap()))
        }
            .onStart { Timber.d("start getting photo by uri: $photoUri") }
            .distinctUntilChanged()
            .onEach { Timber.v("get new photo by uri: $photoUri") }
            .mapLatestResultExceptionToDomain()
            .flowOn(dispatcher)
    }

    suspend fun photoToMultipartBodyPart(
        photoUri: URI,
        applicationContext: Context,
        coroutineDispatcher: CoroutineDispatcher
    ): MultipartBody.Part? {
        val compressedPhotoFile = compressImage(
            imageUri = photoUri,
            applicationContext = applicationContext,
            coroutineDispatcher = coroutineDispatcher,
        ) ?: return null
        val compressedPhoto = compressedPhotoFile.readBytes()
        compressedPhotoFile.delete()

        return MultipartBody.Part.createFormData(
            name = "photo",
            filename = "photo",
            body = compressedPhoto.toRequestBody()
        )
    }
}
