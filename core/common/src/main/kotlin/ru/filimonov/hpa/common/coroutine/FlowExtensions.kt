package ru.filimonov.hpa.common.coroutine

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest

object FlowExtensions {
    fun <T> Flow<T>.mapToResult() =
        this.map { Result.success(it) }.catch {
            emit(Result.failure(it))
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun <T, R> Flow<Result<T>>.mapLatestResult(transform: suspend (T) -> R): Flow<Result<R>> {
        return mapLatest { result: Result<T> ->
            result.map { transform(it) }
        }
    }

    fun <T1, T2> Flow<List<T1>>.mapList(transform: suspend T1.() -> T2): Flow<List<T2>> {
        return map { list -> list.map { it.transform() } }
    }

    fun <T1, T2> Flow<Result<List<T1>>>.mapLatestResultList(transform: suspend T1.() -> T2): Flow<Result<List<T2>>> {
        return mapLatestResult { list -> list.map { it.transform() } }
    }

    fun <T> Flow<Result<T?>>.requireNotNullResult(
        ifNullValue: Throwable = IllegalArgumentException()
    ): Flow<Result<T>> {
        return map {
            if (it.isFailure)
                Result.failure(it.exceptionOrNull()!!)
            else if (it.getOrNull() == null)
                Result.failure(ifNullValue)
            else
                Result.success(it.getOrNull()!!)
        }
    }
}
