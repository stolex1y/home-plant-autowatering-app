package ru.filimonov.hpa.data.remote

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import okhttp3.Response
import retrofit2.HttpException
import ru.filimonov.hpa.common.exception.ServerIsNotAvailableException
import ru.filimonov.hpa.common.utils.mapException
import ru.filimonov.hpa.domain.errors.BadRequestException
import ru.filimonov.hpa.domain.errors.NotAuthenticatedException
import ru.filimonov.hpa.domain.errors.NotFoundException
import ru.filimonov.hpa.domain.errors.UnknownException
import ru.filimonov.hpa.domain.errors.UnknownServerErrorException
import timber.log.Timber
import java.net.ConnectException
import java.net.SocketTimeoutException

fun HttpException.isClientError(): Boolean {
    return code() / 100 == 4
}

fun HttpException.isServerError(): Boolean {
    return code() / 100 == 5
}

fun HttpException.isOk(): Boolean {
    return code() / 100 == 2
}

fun HttpException.isNotFound(): Boolean {
    return code() == 404
}

fun coil.network.HttpException.isNotModified(): Boolean {
    return response.code == 304
}

fun coil.network.HttpException.isNotFound(): Boolean {
    return response.code == 404
}

fun Response.isClientError(): Boolean {
    return code / 100 == 4
}

fun Response.isServerError(): Boolean {
    return code / 100 == 5
}

fun Response.isNotModified(): Boolean {
    return code == 304
}

fun Throwable.mapToDomain(): Throwable {
    when (this) {
        is HttpException -> {
            Timber.e(this, "map to http exception with body: ${this.response()?.body()}")
            when {
                isClientError() -> {
                    return if (code() == 401 || code() == 403)
                        NotAuthenticatedException(this)
                    else if (code() == 404)
                        NotFoundException(this)
                    else
                        BadRequestException(this)
                }

                isServerError() -> return UnknownServerErrorException(this)
            }
        }

        is SocketTimeoutException, is ConnectException -> return ServerIsNotAvailableException(this)
    }
    Timber.e(this, "map exception to domain unknown exception")
    return UnknownException(this)
}

@OptIn(ExperimentalCoroutinesApi::class)
fun <T> Flow<Result<T>>.mapLatestResultExceptionToDomain() = mapLatest { result ->
    result.mapException { ex -> ex.mapToDomain() }
}

fun <T> Result<T>.mapExceptionToDomain() = mapException { ex -> ex.mapToDomain() }
