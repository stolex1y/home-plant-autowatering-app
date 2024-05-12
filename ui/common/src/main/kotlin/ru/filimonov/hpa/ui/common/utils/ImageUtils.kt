package ru.filimonov.hpa.ui.common.utils

import android.content.Context
import android.graphics.Bitmap
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.net.URI
import java.util.UUID

suspend fun compressImage(
    imageUri: URI,
    applicationContext: Context,
    coroutineDispatcher: CoroutineDispatcher
): File? {
    return withContext(coroutineDispatcher) {
        val image = imageUri.getFileBytes(applicationContext, coroutineDispatcher)
            ?: return@withContext null
        val tempFileName = UUID.randomUUID().toString()
        val cacheDir = applicationContext.cacheDir
        val sourceImageTempFile = File.createTempFile(tempFileName, null, cacheDir)
        sourceImageTempFile.writeBytes(image)
        val compressedImageFile =
            Compressor.compress(applicationContext, sourceImageTempFile, coroutineDispatcher) {
                quality(20)
                format(Bitmap.CompressFormat.JPEG)
            }
        val bytes = compressedImageFile.readBytes()
        sourceImageTempFile.delete()
        Timber.d("after compressing ${bytes.size}")
        compressedImageFile
    }
}
