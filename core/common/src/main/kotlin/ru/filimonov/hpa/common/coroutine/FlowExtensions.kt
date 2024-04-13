package ru.filimonov.hpa.common.coroutine

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

private val DEFAULT_SYNC_DELAY = 10.seconds
private val DEFAULT_RETRY_DELAY = 10.seconds
private val MAX_RETRY_DELAY = 100.seconds

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

    @Suppress("UNCHECKED_CAST")
    fun <T1, T2, R> combineResultsTransform(
        flow: Flow<Result<T1>>,
        flow2: Flow<Result<T2>>,
        transform: suspend (T1, T2) -> R
    ): Flow<Result<R>> {
        return combineResults(flow, flow2).mapLatestResult { transform(it[0] as T1, it[1] as T2) }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T1, T2, T3, R> combineResultsTransform(
        flow: Flow<Result<T1>>,
        flow2: Flow<Result<T2>>,
        flow3: Flow<Result<T3>>,
        transform: suspend (T1, T2, T3) -> R
    ): Flow<Result<R>> {
        return combineResults(flow, flow2, flow3).mapLatestResult {
            transform(
                it[0] as T1,
                it[1] as T2,
                it[2] as T3,
            )
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T1, T2, T3, T4, R> combineResultsTransform(
        flow: Flow<Result<T1>>,
        flow2: Flow<Result<T2>>,
        flow3: Flow<Result<T3>>,
        flow4: Flow<Result<T4>>,
        transform: suspend (T1, T2, T3, T4) -> R
    ): Flow<Result<R>> {
        return combineResults(flow, flow2, flow3, flow4).mapLatestResult {
            transform(
                it[0] as T1,
                it[1] as T2,
                it[2] as T3,
                it[3] as T4,
            )
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T1, T2, T3, T4, T5, R> combineResultsTransform(
        flow: Flow<Result<T1>>,
        flow2: Flow<Result<T2>>,
        flow3: Flow<Result<T3>>,
        flow4: Flow<Result<T4>>,
        flow5: Flow<Result<T5>>,
        transform: suspend (T1, T2, T3, T4, T5) -> R
    ): Flow<Result<R>> {
        return combineResults(flow, flow2, flow3, flow4, flow5).mapLatestResult {
            transform(
                it[0] as T1,
                it[1] as T2,
                it[2] as T3,
                it[3] as T4,
                it[4] as T5,
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun <T, R> Flow<Result<T>>.flatMapLatestResult(transform: suspend (value: T) -> Flow<Result<R>>): Flow<Result<R>> =
        flatMapLatest {
            if (it.isFailure)
                flowOf(Result.failure(it.exceptionOrNull()!!))
            else
                transform(it.getOrNull()!!)
        }

    suspend fun <T> FlowCollector<Result<T>>.emitResult(
        retryDelay: Duration = DEFAULT_RETRY_DELAY,
        action: suspend () -> T
    ) {
        runCatching {
            action()
        }.onFailure {
            emit(Result.failure(it))
            if (retryDelay < MAX_RETRY_DELAY) {
                delay(retryDelay)
                emitResult(retryDelay * 2, action)
            }
        }.onSuccess {
            emit(Result.success(it))
        }
    }

    fun <T> makeSyncFlow(
        syncDelay: Duration = DEFAULT_SYNC_DELAY,
        retryDelay: Duration = DEFAULT_RETRY_DELAY,
        emitter: suspend () -> T,
    ): Flow<Result<T>> {
        return flow {
            while (true) {
                emitResult(retryDelay = retryDelay, emitter)
                delay(syncDelay)
            }
        }
    }

    private fun combineResults(vararg flows: Flow<Result<*>>): Flow<Result<Array<*>>> {
        return combine<Result<*>, Result<Array<*>>>(*flows) { flowArray: Array<Result<*>> ->
            val firstFailure = flowArray.firstOrNull { it.isFailure }
            if (firstFailure != null) {
                Result.failure(firstFailure.exceptionOrNull()!!)
            } else {
                Result.success(flowArray.map { it.getOrThrow()!! }.toTypedArray())
            }
        }
    }
}
