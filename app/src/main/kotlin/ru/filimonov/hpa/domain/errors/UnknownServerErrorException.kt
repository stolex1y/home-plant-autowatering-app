package ru.filimonov.hpa.domain.errors

import retrofit2.HttpException

class UnknownServerErrorException(cause: HttpException) : Throwable(cause)
