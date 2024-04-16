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
import ru.filimonov.hpa.domain.errors.UnknownException
import ru.filimonov.hpa.domain.errors.UnknownServerErrorException
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

fun Response.isClientError(): Boolean {
    return code() / 100 == 4
}

fun Response.isServerError(): Boolean {
    return code() / 100 == 5
}

fun Throwable.mapToDomain(): Throwable {
    when (this) {
        is HttpException -> {
            when {
                isClientError() -> {
                    return if (code() == 401 || code() == 403)
                        NotAuthenticatedException(this)
                    else
                        BadRequestException(this)
                }

                isServerError() -> return UnknownServerErrorException(this)
            }
        }

        is SocketTimeoutException, is ConnectException -> return ServerIsNotAvailableException(this)
    }
    return UnknownException(this)
}

@OptIn(ExperimentalCoroutinesApi::class)
fun <T> Flow<Result<T>>.mapLatestResultExceptionToDomain() = mapLatest { result ->
    result.mapException { ex -> ex.mapToDomain() }
}

fun <T> Result<T>.mapExceptionToDomain() = mapException { ex -> ex.mapToDomain() }
