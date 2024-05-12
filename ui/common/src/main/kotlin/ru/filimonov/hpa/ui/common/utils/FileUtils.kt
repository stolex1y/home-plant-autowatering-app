package ru.filimonov.hpa.ui.common.utils

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.net.URI

fun URI.toUri() = Uri.parse(this.toString())

fun Uri.toURI() = URI(this.toString())

@SuppressLint("Recycle")
suspend fun URI.getFileBytes(
    context: Context,
    coroutineDispatcher: CoroutineDispatcher
): ByteArray? {
    return withContext(coroutineDispatcher) {
        val inputStream = if (scheme == ContentResolver.SCHEME_CONTENT) {
            val inputStream = context.contentResolver.openInputStream(this@getFileBytes.toUri())
                ?: return@withContext null
            inputStream
        } else {
            FileInputStream(File(this@getFileBytes))
        }
        val bytes = inputStream.use {
            it.readBytes()
        }
        bytes
    }
}
