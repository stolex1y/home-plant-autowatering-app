package ru.filimonov.hpa.domain.errors

import retrofit2.HttpException

class BadRequestException(cause: HttpException) : Throwable(cause)
