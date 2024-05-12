package ru.filimonov.hpa.domain.errors

import retrofit2.HttpException

class NotFoundException(cause: HttpException) : Throwable(cause)
