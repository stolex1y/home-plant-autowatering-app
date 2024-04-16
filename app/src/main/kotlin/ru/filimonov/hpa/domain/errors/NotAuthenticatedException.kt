package ru.filimonov.hpa.domain.errors

import retrofit2.HttpException

class NotAuthenticatedException(cause: HttpException) : Throwable(cause)
