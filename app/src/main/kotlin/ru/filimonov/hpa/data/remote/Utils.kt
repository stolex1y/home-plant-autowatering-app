package ru.filimonov.hpa.data.remote

import retrofit2.HttpException

fun HttpException.isClientError(): Boolean {
    return code() / 100 == 4
}
