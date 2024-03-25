package ru.filimonov.hpa.common.utils

fun <T1, T2, R> combineResults(
    result1: Result<T1>,
    result2: Result<T2>,
    combine: (T1, T2) -> R
): Result<R> {
    return if (result1.isFailure) {
        Result.failure(result1.exceptionOrNull()!!)
    } else if (result2.isFailure) {
        Result.failure(result2.exceptionOrNull()!!)
    } else {
        runCatching {
            combine(result1.getOrNull()!!, result2.getOrNull()!!)
        }
    }
}

fun <T1, T2> combineResults(
    result1: Result<T1>,
    result2: Result<T2>
): Result<Unit> {
    return combineResults(result1, result2) { _, _ -> }
}
