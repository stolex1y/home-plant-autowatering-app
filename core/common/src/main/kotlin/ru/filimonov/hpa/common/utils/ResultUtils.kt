package ru.filimonov.hpa.common.utils

@Suppress("UNCHECKED_CAST")
fun <T1, T2, R> combineResults(
    result1: Result<T1>,
    result2: Result<T2>,
    combine: (T1, T2) -> R
): Result<R> {
    return combineResults(arrayOf(result1, result2)) {
        combine(
            it[0] as T1,
            it[1] as T2,
        )
    }
}

@Suppress("UNCHECKED_CAST")
suspend fun <T1, T2, R> combineResultsSuspend(
    result1: Result<T1>,
    result2: Result<T2>,
    combine: suspend (T1, T2) -> R
): Result<R> {
    return combineResultsSuspend(arrayOf(result1, result2)) {
        combine(
            it[0] as T1,
            it[1] as T2,
        )
    }
}

fun <T1, T2> combineResults(
    result1: Result<T1>,
    result2: Result<T2>
): Result<Unit> {
    return combineResults(result1, result2) { _, _ -> }
}

@Suppress("UNCHECKED_CAST")
fun <T1, T2, T3, R> combineResults(
    result1: Result<T1>,
    result2: Result<T2>,
    result3: Result<T3>,
    combine: (T1, T2, T3) -> R
): Result<R> {
    return combineResults(arrayOf(result1, result2, result3)) {
        combine(
            it[0] as T1,
            it[1] as T2,
            it[2] as T3,
        )
    }
}

@Suppress("UNCHECKED_CAST")
fun <T1, T2, T3> combineResults(
    result1: Result<T1>,
    result2: Result<T2>,
    result3: Result<T3>,
): Result<Unit> {
    return combineResults(arrayOf(result1, result2, result3)) {}
}

@Suppress("UNCHECKED_CAST")
suspend fun <T1, T2, T3, R> combineResultsSuspend(
    result1: Result<T1>,
    result2: Result<T2>,
    result3: Result<T3>,
    combine: suspend (T1, T2, T3) -> R
): Result<R> {
    return combineResultsSuspend(arrayOf(result1, result2, result3)) {
        combine(
            it[0] as T1,
            it[1] as T2,
            it[2] as T3,
        )
    }
}

private inline fun <reified T, R> combineResults(
    results: Array<Result<T>>,
    crossinline combine: (Array<T>) -> R
): Result<R> {
    val failureResult = results.firstOrNull { it.isFailure }
    return if (failureResult != null) {
        Result.failure(failureResult.exceptionOrNull()!!)
    } else {
        Result.success(combine(results.map { it.getOrNull() as T }.toTypedArray()))
    }
}

private suspend inline fun <reified T, R> combineResultsSuspend(
    results: Array<Result<T>>,
    crossinline combine: suspend (Array<T>) -> R
): Result<R> {
    val failureResult = results.firstOrNull { it.isFailure }
    return if (failureResult != null) {
        Result.failure(failureResult.exceptionOrNull()!!)
    } else {
        Result.success(combine(results.map { it.getOrNull() as T }.toTypedArray()))
    }
}

fun <T> Result<T>.mapException(mapper: (t: Throwable) -> Throwable): Result<T> {
    return if (this.isFailure) {
        Result.failure(mapper(this.exceptionOrNull()!!))
    } else {
        this
    }
}
